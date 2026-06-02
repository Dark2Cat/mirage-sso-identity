package com.mirage.sso.role;

import java.util.List;

public record RoleResponse(
        Long id,
        String code,
        String name,
        String description,
        String dataScope,
        RoleStatus status,
        long userCount,
        List<String> permissions
) {
    public static RoleResponse from(RoleEntity entity, long userCount, List<String> permissions) {
        return new RoleResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getDataScope(),
                entity.getStatus(),
                userCount,
                permissions
        );
    }
}
