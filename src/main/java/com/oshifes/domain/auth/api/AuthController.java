package com.oshifes.domain.auth.api;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.auth.application.JwtTokenProvider;
import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.global.common.ApiResponse;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/test-token")
    public ResponseEntity<ApiResponse<TokenResponse>> testToken(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());
        return ResponseEntity.ok(ApiResponse.ok(TokenResponse.of(token)));
    }
}
