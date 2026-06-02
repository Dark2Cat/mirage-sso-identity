package com.mirage.sso.client;

import jakarta.validation.constraints.NotNull;

public record ClientStatusRequest(@NotNull ClientStatus status) {
}
