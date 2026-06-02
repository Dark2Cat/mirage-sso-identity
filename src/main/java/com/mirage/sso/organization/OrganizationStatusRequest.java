package com.mirage.sso.organization;

import jakarta.validation.constraints.NotNull;

public record OrganizationStatusRequest(@NotNull OrganizationStatus status) {
}
