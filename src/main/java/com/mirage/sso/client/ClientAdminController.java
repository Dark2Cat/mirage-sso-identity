package com.mirage.sso.client;

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
@RequestMapping("/api/admin/clients")
public class ClientAdminController {
    private final ClientService clientService;

    public ClientAdminController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ApiResponse<List<ClientSummary>> listClients() {
        return ApiResponse.success(clientService.listClients());
    }

    @PostMapping
    public ApiResponse<ClientSummary> create(@Valid @RequestBody ClientUpsertRequest request) {
        return ApiResponse.success(clientService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ClientSummary> update(@PathVariable Long id, @Valid @RequestBody ClientUpsertRequest request) {
        return ApiResponse.success(clientService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ClientSummary> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClientStatusRequest request
    ) {
        return ApiResponse.success(clientService.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/reset-secret")
    public ApiResponse<ClientSecretResponse> resetSecret(@PathVariable Long id) {
        return ApiResponse.success(clientService.resetSecret(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/access-assignments")
    public ApiResponse<AppAccessResponse> assignAccess(@Valid @RequestBody AppAccessRequest request) {
        return ApiResponse.success(clientService.assignAccess(request));
    }
}
