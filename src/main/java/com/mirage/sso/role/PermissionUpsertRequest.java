package com.mirage.sso.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PermissionUpsertRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotNull PermissionType type,
        Long parentId,
        String resource,
        String description,
        @NotNull Integer sortOrder,
        @NotNull RoleStatus status
) {
}
