package com.oshifes.domain.auth.api;

import com.oshifes.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CsrfController {

    @Operation(
            summary = "Swagger 테스트용 CSRF 토큰 조회",
            description = "응답의 token 값을 Swagger Authorize의 csrfToken 또는 X-XSRF-TOKEN 헤더에 입력해 쓰기 API를 테스트합니다."
    )
    @SecurityRequirements
    @GetMapping("/csrf")
    public ApiResponse<CsrfResponse> csrf(CsrfToken csrfToken) {
        return ApiResponse.ok(CsrfResponse.from(csrfToken));
    }

    @Getter
    @RequiredArgsConstructor
    public static class CsrfResponse {

        private final String headerName;
        private final String parameterName;
        private final String token;

        public static CsrfResponse from(CsrfToken csrfToken) {
            return new CsrfResponse(
                    csrfToken.getHeaderName(),
                    csrfToken.getParameterName(),
                    csrfToken.getToken()
            );
        }
    }
}
