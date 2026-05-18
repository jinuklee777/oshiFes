package com.oshifes.domain.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuth2TokenRequest(
        @NotBlank(message = "인가 코드는 필수입니다.") String code
) {
}
