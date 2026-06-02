package com.mirage.sso.role;

import jakarta.validation.constraints.NotNull;

public record RoleStatusRequest(@NotNull RoleStatus status) {
}
