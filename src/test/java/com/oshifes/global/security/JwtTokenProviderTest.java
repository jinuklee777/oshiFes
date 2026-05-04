package com.oshifes.global.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktbm90LWZvci1wcm9kdWN0aW9u";

    @Test
    void generateToken_parseClaims_success() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 60_000);

        String token = jwtTokenProvider.generateToken(1L, "USER");
        Claims claims = jwtTokenProvider.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    @Test
    void parseClaimsOrNull_invalidToken_returnsNull() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 60_000);

        Claims claims = jwtTokenProvider.parseClaimsOrNull("invalid.token.value");

        assertThat(claims).isNull();
    }

    @Test
    void parseClaimsOrNull_expiredToken_returnsNull() {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, -1);

        String token = jwtTokenProvider.generateToken(1L, "USER");
        Claims claims = jwtTokenProvider.parseClaimsOrNull(token);

        assertThat(claims).isNull();
    }
}
