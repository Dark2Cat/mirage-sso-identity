package com.mirage.sso.audit;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mirage.sso.common.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuditLogService {
    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public PageResponse<AuditLogResponse> pageAuditLogs(
            long page,
            long size,
            String eventType,
            AuditResult result,
            String keyword
    ) {
        LambdaQueryWrapper<AuditLogEntity> query = new LambdaQueryWrapper<AuditLogEntity>()
                .orderByDesc(AuditLogEntity::getCreatedAt);
        if (StringUtils.hasText(eventType)) {
            query.like(AuditLogEntity::getEventType, eventType);
        }
        if (result != null) {
            query.eq(AuditLogEntity::getResult, result);
        }
        if (StringUtils.hasText(keyword)) {
            query.and(wrapper -> wrapper
                    .like(AuditLogEntity::getActorName, keyword)
                    .or()
                    .like(AuditLogEntity::getEventType, keyword)
                    .or()
                    .like(AuditLogEntity::getTargetType, keyword)
                    .or()
                    .like(AuditLogEntity::getTargetId, keyword)
                    .or()
                    .like(AuditLogEntity::getIpAddress, keyword)
                    .or()
                    .like(AuditLogEntity::getUserAgent, keyword)
                    .or()
                    .like(AuditLogEntity::getFailureReason, keyword));
        }
        Page<AuditLogEntity> pageResult = auditLogMapper.selectPage(
                Page.of(page, size),
                query
        );

        return new PageResponse<>(
                pageResult.getRecords().stream().map(AuditLogResponse::from).toList(),
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal()
        );
    }
}
