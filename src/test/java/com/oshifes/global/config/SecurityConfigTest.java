package com.oshifes.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    void securityConfig_enablesMethodSecurity() {
        assertThat(SecurityConfig.class).hasAnnotation(EnableMethodSecurity.class);
    }

    @Test
    void corsConfigurationSource_allowsConfiguredOriginsWithCredentials() {
        SecurityConfig securityConfig = new SecurityConfig(null, null, null, null);
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins",
                "http://localhost:3000, https://app.example.com");

        CorsConfiguration configuration = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest());

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactly("http://localhost:3000", "https://app.example.com");
        assertThat(configuration.getAllowCredentials()).isTrue();
        assertThat(configuration.getAllowedMethods())
                .contains("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    }
}
