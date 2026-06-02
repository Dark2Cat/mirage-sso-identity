package com.mirage.sso.permission;

import com.mirage.sso.common.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionAdminController {
    private final PermissionService permissionService;

    public PermissionAdminController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> list(@RequestParam(required = false) PermissionType type) {
        return ApiResponse.success(permissionService.listPermissions(type));
    }

    @PostMapping
    public ApiResponse<PermissionResponse> create(@Valid @RequestBody PermissionUpsertRequest request) {
        return ApiResponse.success(permissionService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<PermissionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PermissionUpsertRequest request
    ) {
        return ApiResponse.success(permissionService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<PermissionResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PermissionStatusRequest request
    ) {
        return ApiResponse.success(permissionService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.success();
    }
}
