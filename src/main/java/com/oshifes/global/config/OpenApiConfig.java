package com.oshifes.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "OshiFes API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecuritySchemes(
        {
                @SecurityScheme(
                        name = "bearerAuth",
                        type = SecuritySchemeType.HTTP,
                        scheme = "bearer",
                        bearerFormat = "JWT",
                        in = SecuritySchemeIn.HEADER,
                        description = "JWT access token. Swagger Authorize에는 토큰 값만 입력하세요."
                ),
                @SecurityScheme(
                        name = "csrfToken",
                        type = SecuritySchemeType.APIKEY,
                        in = SecuritySchemeIn.HEADER,
                        paramName = "X-XSRF-TOKEN",
                        description = "POST/PUT/PATCH/DELETE 요청 테스트용 CSRF 토큰. /api/auth/csrf 응답의 token 값을 입력하세요."
                )
        }
)
public class OpenApiConfig {
}
