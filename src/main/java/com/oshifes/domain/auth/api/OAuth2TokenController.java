package com.oshifes.domain.auth.api;

import com.oshifes.domain.auth.api.dto.OAuth2TokenRequest;
import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.auth.application.OAuth2AuthorizationCodeService;
import com.oshifes.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2TokenController {

    private final OAuth2AuthorizationCodeService authorizationCodeService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> token(@Valid @RequestBody OAuth2TokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authorizationCodeService.exchange(request.code())));
    }
}
