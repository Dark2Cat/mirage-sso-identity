package com.mirage.sso.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Security security,
        Cors cors
) {
    public record Security(
            String issuer,
            Duration accessTokenTtl,
            Duration refreshTokenTtl,
            int loginFailureLimit,
            Duration loginLockDuration
    ) {
    }

    public record Cors(
            List<String> allowedOrigins
    ) {
    }
}
