package com.mirage.sso.auth;

import java.util.List;

public record LoginResponse(
        String username,
        String displayName,
        List<String> roles,
        List<String> permissions
) {
}
