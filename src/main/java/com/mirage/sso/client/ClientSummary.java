package com.mirage.sso.client;

public record ClientSummary(
        Long id,
        String clientId,
        String clientName,
        ClientType clientType,
        String redirectUris,
        String postLogoutRedirectUris,
        String grantTypes,
        String scopes,
        Integer accessTokenTtl,
        Integer refreshTokenTtl,
        Boolean requireConsent,
        ClientStatus status
) {
    public static ClientSummary from(ClientEntity entity) {
        return new ClientSummary(
                entity.getId(),
                entity.getClientId(),
                entity.getClientName(),
                entity.getClientType(),
                entity.getRedirectUris(),
                entity.getPostLogoutRedirectUris(),
                entity.getGrantTypes(),
                entity.getScopes(),
                entity.getAccessTokenTtl(),
                entity.getRefreshTokenTtl(),
                entity.getRequireConsent(),
                entity.getStatus()
        );
    }
}
