package com.mirage.sso.permission;

import com.mirage.sso.role.RoleStatus;

public record PermissionResponse(
        Long id,
        String code,
        String name,
        PermissionType type,
        Long parentId,
        String resource,
        String description,
        Integer sortOrder,
        RoleStatus status
) {
    public static PermissionResponse from(PermissionEntity entity) {
        return new PermissionResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getType(),
                entity.getParentId(),
                entity.getResource(),
                entity.getDescription(),
                entity.getSortOrder(),
                entity.getStatus()
        );
    }
}
