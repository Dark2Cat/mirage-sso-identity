package com.mirage.sso.auth;

public record ConsentScopeResponse(
        String code,
        String title,
        String description,
        boolean required,
        String risk
) {
}
