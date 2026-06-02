package com.mirage.sso.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrganizationUpsertRequest(
        Long parentId,
        @NotBlank String name,
        @NotBlank String code,
        String manager,
        @NotNull Integer userCount,
        @NotNull Integer sortOrder,
        @NotNull OrganizationStatus status
) {
}
