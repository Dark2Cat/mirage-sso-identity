package com.mirage.sso.audit;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actorName,
        String eventType,
        String targetType,
        String targetId,
        AuditResult result,
        String ipAddress,
        String userAgent,
        String failureReason,
        String requestId,
        String metadata,
        LocalDateTime createdAt
) {
    public static AuditLogResponse from(AuditLogEntity entity) {
        return new AuditLogResponse(
                entity.getId(),
                entity.getActorName(),
                entity.getEventType(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getResult(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getFailureReason(),
                entity.getRequestId(),
                entity.getMetadata(),
                entity.getCreatedAt()
        );
    }
}
