package com.mirage.sso.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mirage.sso.common.BusinessException;
import com.mirage.sso.common.PageResponse;
import com.mirage.sso.config.AppProperties;
import com.mirage.sso.file.SsoMediaFile;
import com.mirage.sso.file.SsoMediaFileService;
import com.mirage.sso.organization.OrganizationEntity;
import com.mirage.sso.organization.OrganizationMapper;
import com.mirage.sso.role.RoleEntity;
import com.mirage.sso.role.RoleService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final OrganizationMapper organizationMapper;
    private final SsoMediaFileService mediaFileService;
    private final AppProperties properties;

    public UserService(
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            RoleService roleService,
            OrganizationMapper organizationMapper,
            SsoMediaFileService mediaFileService,
            AppProperties properties
    ) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.organizationMapper = organizationMapper;
        this.mediaFileService = mediaFileService;
        this.properties = properties;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return Optional.ofNullable(userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, username)
                        .isNull(UserEntity::getDeletedAt)
                        .last("limit 1")
        ));
    }

    public PageResponse<UserSummary> pageUsers(long page, long size) {
        Page<UserEntity> result = userMapper.selectPage(
                Page.of(page, size),
                new LambdaQueryWrapper<UserEntity>()
                        .isNull(UserEntity::getDeletedAt)
                        .orderByDesc(UserEntity::getCreatedAt)
        );

        return new PageResponse<>(
                result.getRecords().stream().map(this::toSummary).toList(),
                result.getCurrent(),
                result.getSize(),
                result.getTotal()
        );
    }

    @Transactional
    public UserSummary register(String username, String rawPassword) {
        findByUsername(username).ifPresent(existing -> {
            throw new BusinessException("USER_EXISTS", "username already exists");
        });

        LocalDateTime now = LocalDateTime.now();
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setNickname(username);
        entity.setPasswordHash(passwordEncoder.encode(rawPassword));
        entity.setStatus(UserStatus.ACTIVE);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        userMapper.insert(entity);
        roleService.saveUserRoles(entity.getId(), List.of("USER"));
        return toSummary(entity);
    }

    @Transactional
    public UserSummary create(UserUpsertRequest request) {
        findByUsername(request.username()).ifPresent(existing -> {
            throw new BusinessException("USER_EXISTS", "username already exists");
        });
        LocalDateTime now = LocalDateTime.now();
        UserEntity entity = new UserEntity();
        applyRequest(entity, request);
        entity.setPasswordHash(passwordEncoder.encode(
                request.password() == null || request.password().isBlank() ? "Mirage@123456" : request.password()
        ));
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        userMapper.insert(entity);
        roleService.saveUserRoles(entity.getId(), request.roles());
        return toSummary(entity);
    }

    @Transactional
    public UserSummary update(Long id, UserUpsertRequest request) {
        UserEntity entity = getRequired(id);
        if (!entity.getUsername().equals(request.username())) {
            findByUsername(request.username()).ifPresent(existing -> {
                throw new BusinessException("USER_EXISTS", "username already exists");
            });
        }
        applyRequest(entity, request);
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        roleService.saveUserRoles(id, request.roles());
        return toSummary(entity);
    }

    @Transactional
    public UserSummary updateStatus(Long id, UserStatus status) {
        UserEntity entity = getRequired(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        return toSummary(entity);
    }

    @Transactional
    public ResetPasswordResponse resetPassword(Long id) {
        UserEntity entity = getRequired(id);
        String rawPassword = "Mirage@" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        entity.setPasswordHash(passwordEncoder.encode(rawPassword));
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        return new ResetPasswordResponse(rawPassword);
    }

    @Transactional
    public UserSummary updateProfile(String username, ProfileUpdateRequest request) {
        return updateProfile(username, request, null);
    }

    @Transactional
    public UserSummary updateProfile(String username, ProfileUpdateRequest request, MultipartFile avatar) {
        UserEntity entity = findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "user not found"));
        entity.setNickname(request.nickname());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        if (avatar != null && !avatar.isEmpty()) {
            SsoMediaFile mediaFile = mediaFileService.uploadAvatar(avatar, username);
            entity.setAvatarUrl(mediaFile.getUrl());
        } else {
            entity.setAvatarUrl(request.avatarUrl());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
        return toSummary(entity);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        UserEntity entity = findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "user not found"));
        if (!passwordEncoder.matches(request.oldPassword(), entity.getPasswordHash())) {
            throw new BusinessException("BAD_CREDENTIALS", "old password is invalid");
        }
        entity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        entity.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(entity);
    }

    public UserEntity getRequired(Long id) {
        UserEntity entity = userMapper.selectById(id);
        if (entity == null || entity.getDeletedAt() != null) {
            throw new BusinessException("USER_NOT_FOUND", "user not found");
        }
        return entity;
    }

    public UserSummary toSummary(UserEntity entity) {
        String organizationName = null;
        if (entity.getOrganizationId() != null) {
            OrganizationEntity organization = organizationMapper.selectById(entity.getOrganizationId());
            organizationName = organization == null ? null : organization.getName();
        }
        List<String> roles = roleService.rolesByUserId(entity.getId())
                .stream()
                .map(RoleEntity::getCode)
                .toList();
        return new UserSummary(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getEmail(),
                entity.getPhone(),
                resolveAvatarUrl(entity.getAvatarUrl()),
                entity.getOrganizationId(),
                organizationName,
                roles,
                entity.getStatus(),
                entity.getLastLoginAt()
        );
    }

    public String resolveAvatarUrl(String avatarUrl) {
        if (!StringUtils.hasText(avatarUrl)
                || avatarUrl.startsWith("http://")
                || avatarUrl.startsWith("https://")
                || avatarUrl.startsWith("data:")) {
            return avatarUrl;
        }
        if (!avatarUrl.startsWith("/")) {
            return avatarUrl;
        }
        String baseUrl = StringUtils.hasText(properties.minio().publicUrl())
                ? properties.minio().publicUrl()
                : properties.minio().endpoint();
        return baseUrl.replaceAll("/+$", "") + avatarUrl;
    }

    private void applyRequest(UserEntity entity, UserUpsertRequest request) {
        entity.setUsername(request.username());
        entity.setNickname(request.nickname() == null || request.nickname().isBlank() ? request.username() : request.nickname());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setAvatarUrl(request.avatarUrl());
        entity.setOrganizationId(request.organizationId());
        entity.setStatus(request.status());
    }
}
