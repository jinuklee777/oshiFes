package com.oshifes.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void openApiConfig_exposesSwaggerAuthorizeSchemesForJwtAndCsrf() {
        SecuritySchemes securitySchemes = OpenApiConfig.class.getAnnotation(SecuritySchemes.class);

        assertThat(securitySchemes).isNotNull();
        assertThat(securitySchemes.value())
                .extracting(SecurityScheme::name)
                .contains("bearerAuth", "csrfToken");
        assertThat(securitySchemes.value())
                .filteredOn(securityScheme -> "bearerAuth".equals(securityScheme.name()))
                .singleElement()
                .satisfies(securityScheme -> {
                    assertThat(securityScheme.type()).isEqualTo(SecuritySchemeType.HTTP);
                    assertThat(securityScheme.scheme()).isEqualTo("bearer");
                    assertThat(securityScheme.bearerFormat()).isEqualTo("JWT");
                });
        assertThat(securitySchemes.value())
                .filteredOn(securityScheme -> "csrfToken".equals(securityScheme.name()))
                .singleElement()
                .satisfies(securityScheme -> {
                    assertThat(securityScheme.type()).isEqualTo(SecuritySchemeType.APIKEY);
                    assertThat(securityScheme.in()).isEqualTo(SecuritySchemeIn.HEADER);
                    assertThat(securityScheme.paramName()).isEqualTo("X-XSRF-TOKEN");
                });
    }
}
