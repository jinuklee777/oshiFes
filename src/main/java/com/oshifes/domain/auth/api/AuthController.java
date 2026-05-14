package com.oshifes.domain.auth.api;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.auth.application.AuthService;
import com.oshifes.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Profile("local")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Swagger 테스트용 JWT 발급",
            description = "local 프로필에서만 활성화됩니다. 응답의 accessToken 값을 Authorization: Bearer 헤더 또는 Swagger Authorize의 bearerAuth에 입력해 보호 API를 테스트합니다."
    )
    @SecurityRequirements
    @PostMapping("/test-token")
    public ResponseEntity<ApiResponse<TokenResponse>> testToken(
            @Parameter(description = "테스트 토큰을 발급할 사용자 ID", example = "1")
            @RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(authService.generateTestToken(userId)));
    }
}
