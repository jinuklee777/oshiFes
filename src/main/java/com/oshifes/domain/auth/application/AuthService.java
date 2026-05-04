package com.oshifes.domain.auth.application;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import com.oshifes.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse generateTestToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole().name());
        return TokenResponse.of(token);
    }
}
