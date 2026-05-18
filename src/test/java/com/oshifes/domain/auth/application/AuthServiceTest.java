package com.oshifes.domain.auth.application;

import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.domain.user.entity.UserRole;
import com.oshifes.global.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktbm90LWZvci1wcm9kdWN0aW9u";

    @Mock
    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET, 60_000);
        authService = new AuthService(userRepository, jwtTokenProvider);
    }

    @Test
    void generateTestToken_withoutUserId_createsLocalTestUserWhenMissing() {
        User savedUser = user();
        given(userRepository.findByProviderAndProviderId("local-test", "default"))
                .willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        String token = authService.generateTestToken(null).getAccessToken();
        Claims claims = jwtTokenProvider.parseClaimsOrNull(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void generateTestToken_withoutUserId_reusesLocalTestUser() {
        User user = user();
        given(userRepository.findByProviderAndProviderId("local-test", "default"))
                .willReturn(Optional.of(user));

        String token = authService.generateTestToken(null).getAccessToken();
        Claims claims = jwtTokenProvider.parseClaimsOrNull(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    private User user() {
        User user = User.createNewUser("local-test", "default", "로컬 테스트 유저", null, UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }
}
