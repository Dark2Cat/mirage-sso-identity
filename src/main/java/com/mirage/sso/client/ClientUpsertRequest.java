package com.mirage.sso.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientUpsertRequest(
        @NotBlank String clientId,
        String clientSecret,
        @NotBlank String clientName,
        @NotNull ClientType clientType,
        @NotBlank String redirectUris,
        String postLogoutRedirectUris,
        @NotBlank String grantTypes,
        @NotBlank String scopes,
        @NotNull Integer accessTokenTtl,
        @NotNull Integer refreshTokenTtl,
        Boolean requireConsent,
        @NotNull ClientStatus status
) {
}
