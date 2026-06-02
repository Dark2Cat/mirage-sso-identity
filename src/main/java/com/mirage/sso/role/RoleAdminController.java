package com.mirage.sso.role;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {
    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> listRoles() {
        return ApiResponse.success(roleService.listRoles());
    }

    @PostMapping
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody RoleUpsertRequest request) {
        return ApiResponse.success(roleService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<RoleResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody RoleStatusRequest request) {
        return ApiResponse.success(roleService.updateStatus(id, request.status()));
    }

    @PutMapping("/{id}/permissions")
    public ApiResponse<RoleResponse> updatePermissions(
            @PathVariable Long id,
            @Valid @RequestBody RolePermissionRequest request
    ) {
        return ApiResponse.success(roleService.updatePermissions(id, request.permissions()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success();
    }
}
