package com.mirage.sso.user;

import java.time.LocalDateTime;

public record UserSummary(
        Long id,
        String username,
        String nickname,
        String email,
        String phone,
        String avatarUrl,
        Long organizationId,
        String organizationName,
        java.util.List<String> roles,
        UserStatus status,
        LocalDateTime lastLoginAt
) {
    public static UserSummary from(UserEntity entity) {
        return from(entity, null, java.util.List.of());
    }

    public static UserSummary from(UserEntity entity, String organizationName, java.util.List<String> roles) {
        return new UserSummary(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAvatarUrl(),
                entity.getOrganizationId(),
                organizationName,
                roles,
                entity.getStatus(),
                entity.getLastLoginAt()
        );
    }
}
