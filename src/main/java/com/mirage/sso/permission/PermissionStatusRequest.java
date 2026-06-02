package com.mirage.sso.permission;

import com.mirage.sso.role.RoleStatus;
import jakarta.validation.constraints.NotNull;

public record PermissionStatusRequest(@NotNull RoleStatus status) {
}
