package com.mirage.sso.client;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AppAccessRequest(
        Long userId,
        String roleCode,
        @NotNull List<String> appCodes
) {
}
