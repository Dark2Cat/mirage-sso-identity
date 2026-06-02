package com.mirage.sso.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RoleUpsertRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        String dataScope,
        @NotNull RoleStatus status,
        List<String> permissions
) {
}
