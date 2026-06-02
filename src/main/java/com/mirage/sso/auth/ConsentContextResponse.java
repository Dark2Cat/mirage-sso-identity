package com.mirage.sso.auth;

import java.util.List;

public record ConsentContextResponse(
        ConsentClientResponse client,
        CurrentUserResponse user,
        List<ConsentScopeResponse> scopes
) {
}
