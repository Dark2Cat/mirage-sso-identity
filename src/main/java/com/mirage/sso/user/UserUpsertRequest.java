package com.mirage.sso.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UserUpsertRequest(
        @NotBlank String username,
        String password,
        String nickname,
        String email,
        String phone,
        String avatarUrl,
        Long organizationId,
        List<String> roles,
        @NotNull UserStatus status
) {
}
