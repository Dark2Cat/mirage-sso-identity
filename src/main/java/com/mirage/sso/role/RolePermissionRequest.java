package com.mirage.sso.role;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RolePermissionRequest(@NotNull List<String> permissions) {
}
