package com.oshifes.domain.auth.application;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.domain.user.entity.UserRole;
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

    private static final String TEST_PROVIDER = "local-test";
    private static final String TEST_PROVIDER_ID = "default";

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse generateTestToken(Long userId) {
        User user = userId == null
                ? findOrCreateTestUser()
                : userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole().name());
        return TokenResponse.of(token);
    }

    private User findOrCreateTestUser() {
        return userRepository.findByProviderAndProviderId(TEST_PROVIDER, TEST_PROVIDER_ID)
                .orElseGet(() -> userRepository.save(User.createNewUser(
                        TEST_PROVIDER,
                        TEST_PROVIDER_ID,
                        "로컬 테스트 유저",
                        null,
                        UserRole.USER
                )));
    }
}
