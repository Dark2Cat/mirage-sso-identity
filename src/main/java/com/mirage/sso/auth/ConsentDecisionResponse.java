package com.mirage.sso.auth;

import java.util.List;

public record ConsentDecisionResponse(
        String clientId,
        boolean approved,
        List<String> scopes,
        String redirectUri,
        String result
) {
}
