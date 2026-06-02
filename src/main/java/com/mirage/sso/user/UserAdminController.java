package com.mirage.sso.user;

import com.mirage.sso.common.ApiResponse;
import com.mirage.sso.common.BusinessException;
import com.mirage.sso.common.PageResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResponse<UserSummary>> users(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size
    ) {
        return ApiResponse.success(userService.pageUsers(page, size));
    }

    @PostMapping
    public ApiResponse<UserSummary> create(@Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserSummary> update(@PathVariable Long id, @Valid @RequestBody UserUpsertRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserSummary> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
        return ApiResponse.success(userService.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/reset-password")
    public ApiResponse<ResetPasswordResponse> resetPassword(@PathVariable Long id) {
        return ApiResponse.success(userService.resetPassword(id));
    }

    @PutMapping("/me/profile")
    public ApiResponse<UserSummary> updateProfile(
            Principal principal,
            @RequestBody ProfileUpdateRequest request
    ) {
        String username = currentUsername(principal);
        return ApiResponse.success(userService.updateProfile(username, request));
    }

    @PostMapping("/me/password")
    public ApiResponse<Void> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        String username = currentUsername(principal);
        userService.changePassword(username, request);
        return ApiResponse.success();
    }

    private String currentUsername(Principal principal) {
        if (principal == null) {
            throw new BusinessException("UNAUTHORIZED", "user is not authenticated");
        }
        return principal.getName();
    }
}
