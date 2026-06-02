package com.mirage.sso.file;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mirage.sso.common.BusinessException;
import com.mirage.sso.config.AppProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
* @author jotiancheng
* @description 针对表【sso_media_file(文件管理实体类)】的数据库操作Service实现
* @createDate 2026-06-02 17:46:37
*/
@Service
public class SsoMediaFileServiceImpl extends ServiceImpl<SsoMediaFileMapper, SsoMediaFile>
    implements SsoMediaFileService{

    private static final int FILE_TYPE_IMAGE = 0;
    private static final int STATUS_NORMAL = 0;
    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");

    private final MinioClient minioClient;
    private final AppProperties properties;

    public SsoMediaFileServiceImpl(MinioClient minioClient, AppProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public SsoMediaFile uploadAvatar(MultipartFile file, String uploadBy) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("EMPTY_FILE", "avatar file is empty");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new BusinessException("INVALID_FILE_TYPE", "avatar must be an image");
        }

        try {
            byte[] bytes = file.getBytes();
            String fileId = DigestUtils.md5DigestAsHex(bytes);
            SsoMediaFile existingFile = getById(fileId);
            if (existingFile != null) {
                ensurePublicBucket(resolveBucket(existingFile));
                fillMissingUrl(existingFile);
                return existingFile;
            }

            String extension = resolveExtension(file, contentType);
            String bucket = properties.minio().bucket();
            String objectName = DATE_PATH_FORMATTER.format(LocalDate.now()) + "/" + fileId + "." + extension;
            ensurePublicBucket(bucket);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(bytes), (long) bytes.length, -1L)
                    .contentType(contentType)
                    .build());

            Date now = new Date();
            SsoMediaFile mediaFile = new SsoMediaFile();
            mediaFile.setId(fileId);
            mediaFile.setFilename(file.getOriginalFilename());
            mediaFile.setFileType(FILE_TYPE_IMAGE);
            mediaFile.setBucket(bucket);
            mediaFile.setFilePath(objectName);
            mediaFile.setFileId(fileId);
            mediaFile.setUrl(buildUrl(bucket, objectName));
            mediaFile.setUploadBy(uploadBy);
            mediaFile.setUploadDate(now);
            mediaFile.setChangeDate(now);
            mediaFile.setStatus(STATUS_NORMAL);
            mediaFile.setFileSize(file.getSize());
            saveOrUpdate(mediaFile);
            return mediaFile;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("FILE_UPLOAD_FAILED", "failed to upload avatar to minio");
        }
    }

    private void fillMissingUrl(SsoMediaFile mediaFile) {
        if (StringUtils.hasText(mediaFile.getUrl()) || !StringUtils.hasText(mediaFile.getFilePath())) {
            return;
        }
        String bucket = resolveBucket(mediaFile);
        mediaFile.setBucket(bucket);
        mediaFile.setUrl(buildUrl(bucket, mediaFile.getFilePath()));
        mediaFile.setChangeDate(new Date());
        updateById(mediaFile);
    }

    private String resolveBucket(SsoMediaFile mediaFile) {
        return StringUtils.hasText(mediaFile.getBucket()) ? mediaFile.getBucket() : properties.minio().bucket();
    }

    /**
     * 确保bucket存在并允许匿名读取对象
     * @param bucket  bucket
     * @throws Exception
     */
    private void ensurePublicBucket(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build());
        }
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucket)
                .config(publicReadPolicy(bucket))
                .build());
    }

    private String publicReadPolicy(String bucket) {
        return """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
    }

    private String resolveExtension(MultipartFile file, String contentType) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (StringUtils.hasText(extension) && extension.matches("[A-Za-z0-9]{1,10}")) {
            return extension.toLowerCase(Locale.ROOT);
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/svg+xml" -> "svg";
            default -> "img";
        };
    }

    private String buildUrl(String bucket, String objectName) {
        String path = "/" + bucket + "/" + objectName;
        String publicUrl = properties.minio().publicUrl();
        if (!StringUtils.hasText(publicUrl)) {
            return path;
        }
        return publicUrl.replaceAll("/+$", "") + path;
    }
}
