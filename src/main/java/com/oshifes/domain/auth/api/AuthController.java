package com.oshifes.domain.auth.api;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.auth.application.AuthService;
import com.oshifes.global.common.ApiResponse;
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

    @PostMapping("/test-token")
    public ResponseEntity<ApiResponse<TokenResponse>> testToken(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(authService.generateTestToken(userId)));
    }
}
