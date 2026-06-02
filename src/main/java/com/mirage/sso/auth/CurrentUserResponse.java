package com.mirage.sso.auth;

import java.util.List;

public record CurrentUserResponse(
        Long id,
        String username,
        String nickname,
        String email,
        String phone,
        String avatarUrl,
        List<String> roles,
        List<String> permissions,
        List<String> apps
) {
}
