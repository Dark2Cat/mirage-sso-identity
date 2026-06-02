package com.mirage.sso.file;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 文件管理实体类
 * @TableName sso_media_file
 */
@TableName(value ="sso_media_file")
public class SsoMediaFile {
    /**
     * 文件id，md5值
     */
    @TableId
    private String id;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件类型（0-图片，1-文档，2-视频）
     */
    private Integer fileType;

    /**
     * 标签
     */
    private String tags;

    /**
     * 存储目录
     */
    private String bucket;

    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 上传人
     */
    private String uploadBy;

    /**
     * 上传时间
     */
    private Date uploadDate;

    /**
     * 修改时间
     */
    private Date changeDate;

    /**
     * 状态：0：正常，1：不展示
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditMind;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件id，md5值
     */
    public String getId() {
        return id;
    }

    /**
     * 文件id，md5值
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 文件名称
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 文件名称
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 文件类型（0-图片，1-文档，2-视频）
     */
    public Integer getFileType() {
        return fileType;
    }

    /**
     * 文件类型（0-图片，1-文档，2-视频）
     */
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    /**
     * 标签
     */
    public String getTags() {
        return tags;
    }

    /**
     * 标签
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 存储目录
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * 存储目录
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * 存储路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 存储路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 文件id
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * 文件id
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * 文件访问地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 文件访问地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 上传人
     */
    public String getUploadBy() {
        return uploadBy;
    }

    /**
     * 上传人
     */
    public void setUploadBy(String uploadBy) {
        this.uploadBy = uploadBy;
    }

    /**
     * 上传时间
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    /**
     * 上传时间
     */
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * 修改时间
     */
    public Date getChangeDate() {
        return changeDate;
    }

    /**
     * 修改时间
     */
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    /**
     * 状态：0：正常，1：不展示
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 状态：0：正常，1：不展示
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 审核状态
     */
    public String getAuditStatus() {
        return auditStatus;
    }

    /**
     * 审核状态
     */
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    /**
     * 审核意见
     */
    public String getAuditMind() {
        return auditMind;
    }

    /**
     * 审核意见
     */
    public void setAuditMind(String auditMind) {
        this.auditMind = auditMind;
    }

    /**
     * 文件大小
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * 文件大小
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SsoMediaFile other = (SsoMediaFile) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFilename() == null ? other.getFilename() == null : this.getFilename().equals(other.getFilename()))
            && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getBucket() == null ? other.getBucket() == null : this.getBucket().equals(other.getBucket()))
            && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
            && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
            && (this.getUrl() == null ? other.getUrl() == null : this.getUrl().equals(other.getUrl()))
            && (this.getUploadBy() == null ? other.getUploadBy() == null : this.getUploadBy().equals(other.getUploadBy()))
            && (this.getUploadDate() == null ? other.getUploadDate() == null : this.getUploadDate().equals(other.getUploadDate()))
            && (this.getChangeDate() == null ? other.getChangeDate() == null : this.getChangeDate().equals(other.getChangeDate()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getAuditStatus() == null ? other.getAuditStatus() == null : this.getAuditStatus().equals(other.getAuditStatus()))
            && (this.getAuditMind() == null ? other.getAuditMind() == null : this.getAuditMind().equals(other.getAuditMind()))
            && (this.getFileSize() == null ? other.getFileSize() == null : this.getFileSize().equals(other.getFileSize()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFilename() == null) ? 0 : getFilename().hashCode());
        result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getBucket() == null) ? 0 : getBucket().hashCode());
        result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getUrl() == null) ? 0 : getUrl().hashCode());
        result = prime * result + ((getUploadBy() == null) ? 0 : getUploadBy().hashCode());
        result = prime * result + ((getUploadDate() == null) ? 0 : getUploadDate().hashCode());
        result = prime * result + ((getChangeDate() == null) ? 0 : getChangeDate().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getAuditStatus() == null) ? 0 : getAuditStatus().hashCode());
        result = prime * result + ((getAuditMind() == null) ? 0 : getAuditMind().hashCode());
        result = prime * result + ((getFileSize() == null) ? 0 : getFileSize().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", filename=").append(filename);
        sb.append(", fileType=").append(fileType);
        sb.append(", tags=").append(tags);
        sb.append(", bucket=").append(bucket);
        sb.append(", filePath=").append(filePath);
        sb.append(", fileId=").append(fileId);
        sb.append(", url=").append(url);
        sb.append(", uploadBy=").append(uploadBy);
        sb.append(", uploadDate=").append(uploadDate);
        sb.append(", changeDate=").append(changeDate);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", auditStatus=").append(auditStatus);
        sb.append(", auditMind=").append(auditMind);
        sb.append(", fileSize=").append(fileSize);
        sb.append("]");
        return sb.toString();
    }
}