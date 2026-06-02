package com.mirage.sso.auth;

import com.mirage.sso.common.BusinessException;
import com.mirage.sso.role.PermissionEntity;
import com.mirage.sso.role.RoleEntity;
import com.mirage.sso.role.RoleService;
import com.mirage.sso.client.AppAccessMapper;
import com.mirage.sso.user.UserEntity;
import com.mirage.sso.user.UserService;
import com.mirage.sso.user.UserStatus;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AppAccessMapper appAccessMapper;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            RoleService roleService,
            AppAccessMapper appAccessMapper
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.appAccessMapper = appAccessMapper;
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userService.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("BAD_CREDENTIALS", "username or password is invalid"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("USER_NOT_ACTIVE", "user is not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("BAD_CREDENTIALS", "username or password is invalid");
        }

        return new LoginResponse(
                user.getUsername(),
                user.getNickname(),
                roleCodes(user),
                permissionCodes(user)
        );
    }

    public CurrentUserResponse currentUser(String username) {
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "current user not found"));

        return new CurrentUserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                roleCodes(user),
                permissionCodes(user),
                appCodes(user)
        );
    }

    private List<String> roleCodes(UserEntity user) {
        List<String> roles = roleService.rolesByUserId(user.getId())
                .stream()
                .map(RoleEntity::getCode)
                .toList();
        return roles.isEmpty() ? List.of("USER") : roles;
    }

    private List<String> permissionCodes(UserEntity user) {
        return roleService.permissionsByUserId(user.getId())
                .stream()
                .map(PermissionEntity::getCode)
                .distinct()
                .toList();
    }

    private List<String> appCodes(UserEntity user) {
        List<String> directApps = appAccessMapper.selectUserApps(user.getId());
        if (!directApps.isEmpty()) {
            return directApps;
        }
        List<String> appsFromPermissions = permissionCodes(user).stream()
                .filter(permission -> permission.startsWith("app:") && permission.endsWith(":access"))
                .map(permission -> permission.substring(4, permission.length() - 7))
                .toList();
        return appsFromPermissions.isEmpty() ? List.of("oa", "crm") : appsFromPermissions;
    }
}
