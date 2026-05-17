package com.oshifes.domain.auth.application;

import com.oshifes.global.error.CustomException;
import com.oshifes.global.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2AuthorizationCodeServiceTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktbm90LWZvci1wcm9kdWN0aW9u";

    private final OAuth2AuthorizationCodeService service = new OAuth2AuthorizationCodeService(
            new JwtTokenProvider(SECRET, 60_000),
            Clock.fixed(Instant.parse("2026-05-17T00:00:00Z"), ZoneId.of("Asia/Seoul"))
    );

    @Test
    void exchange_validCode_returnsAccessTokenAndConsumesCode() {
        String code = service.issueCode(1L, "USER");

        assertThat(service.exchange(code).getAccessToken()).isNotBlank();
        assertThatThrownBy(() -> service.exchange(code))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void exchange_unknownCode_throwsException() {
        assertThatThrownBy(() -> service.exchange("unknown"))
                .isInstanceOf(CustomException.class);
    }
}
