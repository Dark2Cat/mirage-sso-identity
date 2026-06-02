package com.mirage.sso.auth;

import com.mirage.sso.common.ApiResponse;
import com.mirage.sso.common.BusinessException;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth/consent")
public class OAuthConsentController {
    private final OAuthConsentService consentService;

    public OAuthConsentController(OAuthConsentService consentService) {
        this.consentService = consentService;
    }

    /**
     * 获取授权上下文
     *
     * @param clientId 客户端ID
     * @param principal 当前用户
     * @return 授权上下文
     */
    @GetMapping
    public ApiResponse<ConsentContextResponse> context(
            @RequestParam(name = "client_id") String clientId,
            Principal principal
    ) {
        if (principal == null) {
            throw new BusinessException("UNAUTHORIZED", "user is not authenticated");
        }
        String username = principal.getName();
        return ApiResponse.success(consentService.context(clientId, username));
    }

    /**
     * 提交授权结果
     *
     * @param request 授权结果
     * @return 授权结果
     */
    @PostMapping
    public ApiResponse<ConsentDecisionResponse> decide(@Valid @RequestBody ConsentDecisionRequest request) {
        return ApiResponse.success(consentService.decide(request));
    }
}
