package com.oshifes.domain.auth.application;

import com.oshifes.domain.auth.api.dto.TokenResponse;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import com.oshifes.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class OAuth2AuthorizationCodeService {

    private static final Duration CODE_TTL = Duration.ofMinutes(3);

    private final JwtTokenProvider jwtTokenProvider;
    private final Clock clock;
    private final ConcurrentMap<String, AuthorizationCode> codes = new ConcurrentHashMap<>();

    public String issueCode(Long userId, String role) {
        purgeExpiredCodes();
        String code = UUID.randomUUID().toString();
        codes.put(code, new AuthorizationCode(userId, role, Instant.now(clock).plus(CODE_TTL)));
        return code;
    }

    public TokenResponse exchange(String code) {
        AuthorizationCode authorizationCode = codes.remove(code);
        if (authorizationCode == null || authorizationCode.isExpired(Instant.now(clock))) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return TokenResponse.of(jwtTokenProvider.generateToken(
                authorizationCode.userId(),
                authorizationCode.role()
        ));
    }

    private void purgeExpiredCodes() {
        Instant now = Instant.now(clock);
        codes.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    private record AuthorizationCode(Long userId, String role, Instant expiresAt) {

        boolean isExpired(Instant now) {
            return !expiresAt.isAfter(now);
        }
    }
}
