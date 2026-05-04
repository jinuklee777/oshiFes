package com.oshifes.domain.auth.api;

import com.oshifes.global.common.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class CsrfController {

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
