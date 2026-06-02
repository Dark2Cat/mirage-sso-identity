package com.mirage.sso.organization;

public record OrganizationResponse(
        Long id,
        Long parentId,
        String name,
        String code,
        String manager,
        Integer userCount,
        Integer sortOrder,
        OrganizationStatus status
) {
    public static OrganizationResponse from(OrganizationEntity entity) {
        return new OrganizationResponse(
                entity.getId(),
                entity.getParentId(),
                entity.getName(),
                entity.getCode(),
                entity.getManager(),
                entity.getUserCount(),
                entity.getSortOrder(),
                entity.getStatus()
        );
    }
}
