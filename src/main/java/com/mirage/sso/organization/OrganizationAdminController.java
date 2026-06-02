package com.mirage.sso.organization;

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
@RequestMapping("/api/admin/organizations")
public class OrganizationAdminController {
    private final OrganizationService organizationService;

    public OrganizationAdminController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ApiResponse<List<OrganizationResponse>> list() {
        return ApiResponse.success(organizationService.listOrganizations());
    }

    @PostMapping
    public ApiResponse<OrganizationResponse> create(@Valid @RequestBody OrganizationUpsertRequest request) {
        return ApiResponse.success(organizationService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<OrganizationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationUpsertRequest request
    ) {
        return ApiResponse.success(organizationService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OrganizationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationStatusRequest request
    ) {
        return ApiResponse.success(organizationService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return ApiResponse.success();
    }
}
