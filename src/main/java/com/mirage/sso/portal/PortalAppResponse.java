package com.mirage.sso.portal;

public record PortalAppResponse(
        String code,
        String name,
        String description,
        String category,
        String entryUrl,
        String logoUrl,
        String techTags,
        AppVisibility visibility,
        AppStatus status
) {
    public static PortalAppResponse from(AppEntity entity) {
        return new PortalAppResponse(
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getEntryUrl(),
                entity.getLogoUrl(),
                entity.getTechTags(),
                entity.getVisibility(),
                entity.getStatus()
        );
    }
}
