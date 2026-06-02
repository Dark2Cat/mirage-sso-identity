package com.mirage.sso.auth;

public record ConsentClientResponse(
        String clientId,
        String name,
        String owner,
        String redirectUri,
        String homepage
) {
}
