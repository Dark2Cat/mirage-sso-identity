package com.mirage.sso.user;

public record ProfileUpdateRequest(
        String nickname,
        String email,
        String phone,
        String avatarUrl
) {
}
