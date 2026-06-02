package com.mirage.sso.portal;

import com.mirage.sso.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal")
public class PortalController {
    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/apps")
    public ApiResponse<List<PortalAppResponse>> apps() {
        return ApiResponse.success(portalService.listVisibleApps());
    }
}
