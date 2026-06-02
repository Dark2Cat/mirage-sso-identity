package com.mirage.sso.client;

import java.util.List;

public record AppAccessResponse(
        Long userId,
        String roleCode,
        List<String> appCodes
) {
}
