package com.mirage.sso.role;

import jakarta.validation.constraints.NotNull;

public record PermissionStatusRequest(@NotNull RoleStatus status) {
}
