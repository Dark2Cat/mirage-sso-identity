package com.mirage.sso.file;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
* @author jotiancheng
* @description 针对表【sso_media_file(文件管理实体类)】的数据库操作Service
* @createDate 2026-06-02 17:46:37
*/
public interface SsoMediaFileService extends IService<SsoMediaFile> {
    SsoMediaFile uploadAvatar(MultipartFile file, String uploadBy);
}
