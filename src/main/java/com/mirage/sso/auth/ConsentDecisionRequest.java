package com.mirage.sso.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ConsentDecisionRequest(
        @NotBlank String clientId,
        @NotNull Boolean approved,
        List<String> scopes
) {
}
