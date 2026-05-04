package com.oshifes.domain.auth.api.dto;

import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder(access = PRIVATE)
public class TokenResponse {

    private final String accessToken;

    public static TokenResponse of(String accessToken) {
        return TokenResponse.builder().accessToken(accessToken).build();
    }
}
