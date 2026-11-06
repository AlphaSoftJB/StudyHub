package com.alphasoft.studyhub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.util.List;

/**
 * Typed binding for all studyhub.* properties in application.yml.
 * Secrets (jwt.access-secret, jwt.refresh-secret) must be supplied
 * via environment variables in production — never hard-coded.
 */
@ConfigurationProperties(prefix = "studyhub")
public record StudyHubProperties(
        Jwt jwt,
        Cookie cookie,
        Cors cors,
        Pilot pilot,
        Ai ai
) {

    public record Jwt(
            String accessSecret,
            @DefaultValue("900000") long accessExpiryMs,      // 15 min default
            String refreshSecret,
            @DefaultValue("604800000") long refreshExpiryMs   // 7 days default
    ) {}

    public record Cookie(
            @DefaultValue("localhost") String domain,
            @DefaultValue("false") boolean secure,
            @DefaultValue("Strict") String sameSite
    ) {}

    public record Cors(
            @DefaultValue("http://localhost:5173") List<String> allowedOrigins
    ) {}

    public record Pilot(
            @DefaultValue("10") int maxLearners
    ) {}

    public record Ai(
            @DefaultValue("25.00") BigDecimal weeklyCeilingUsd,
            @DefaultValue("20.00") BigDecimal alertThresholdUsd
    ) {}
}
