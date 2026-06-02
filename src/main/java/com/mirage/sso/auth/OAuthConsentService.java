package com.mirage.sso.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mirage.sso.client.ClientEntity;
import com.mirage.sso.client.ClientMapper;
import com.mirage.sso.common.BusinessException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OAuthConsentService {
    private final ClientMapper clientMapper;
    private final AuthService authService;

    public OAuthConsentService(ClientMapper clientMapper, AuthService authService) {
        this.clientMapper = clientMapper;
        this.authService = authService;
    }

    public ConsentContextResponse context(String clientId, String username) {
        ClientEntity client = findClient(clientId);
        return new ConsentContextResponse(
                toClientResponse(client),
                authService.currentUser(username),
                parseScopes(client.getScopes()).stream().map(this::toScopeResponse).toList()
        );
    }

    public ConsentDecisionResponse decide(ConsentDecisionRequest request) {
        ClientEntity client = findClient(request.clientId());
        List<String> requestedScopes = parseScopes(client.getScopes());
        List<String> grantedScopes = Boolean.TRUE.equals(request.approved())
                ? normalizeGrantedScopes(request.scopes(), requestedScopes)
                : List.of();
        return new ConsentDecisionResponse(
                client.getClientId(),
                Boolean.TRUE.equals(request.approved()),
                grantedScopes,
                firstValue(client.getRedirectUris()),
                Boolean.TRUE.equals(request.approved()) ? "AUTHORIZED" : "ACCESS_DENIED"
        );
    }

    private ClientEntity findClient(String clientId) {
        ClientEntity client = clientMapper.selectOne(
                new LambdaQueryWrapper<ClientEntity>()
                        .eq(ClientEntity::getClientId, clientId)
                        .last("limit 1")
        );
        if (client == null) {
            throw new BusinessException("CLIENT_NOT_FOUND", "client not found");
        }
        return client;
    }

    private ConsentClientResponse toClientResponse(ClientEntity client) {
        return new ConsentClientResponse(
                client.getClientId(),
                client.getClientName(),
                "Mirage Platform",
                firstValue(client.getRedirectUris()),
                firstValue(client.getPostLogoutRedirectUris())
        );
    }

    private ConsentScopeResponse toScopeResponse(String code) {
        return switch (code) {
            case "openid" -> new ConsentScopeResponse(code, "身份标识", "确认当前登录账号的唯一身份。", true, "low");
            case "profile" -> new ConsentScopeResponse(code, "基础资料", "读取昵称、头像、用户名等基础资料。", true, "low");
            case "email" -> new ConsentScopeResponse(code, "邮箱地址", "读取账号邮箱，用于应用内通知和账号识别。", false, "medium");
            case "offline_access" -> new ConsentScopeResponse(code, "离线访问", "允许应用在你离开后刷新访问令牌。", false, "high");
            default -> new ConsentScopeResponse(code, "应用权限", "允许应用使用 " + code + " 权限。", false, "medium");
        };
    }

    private List<String> normalizeGrantedScopes(List<String> grantedScopes, List<String> requestedScopes) {
        Set<String> requested = new LinkedHashSet<>(requestedScopes);
        Set<String> granted = new LinkedHashSet<>(grantedScopes == null ? List.of() : grantedScopes);
        granted.retainAll(requested);
        requestedScopes.stream()
                .filter(scope -> "openid".equals(scope) || "profile".equals(scope))
                .forEach(granted::add);
        return List.copyOf(granted);
    }

    private List<String> parseScopes(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        String normalized = value
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "");
        return Arrays.stream(normalized.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private String firstValue(String value) {
        return parseScopes(value).stream().findFirst().orElse("");
    }
}
