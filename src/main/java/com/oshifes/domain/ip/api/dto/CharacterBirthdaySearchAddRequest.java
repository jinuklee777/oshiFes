package com.oshifes.domain.ip.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CharacterBirthdaySearchAddRequest(
        @NotBlank(message = "query는 필수입니다.") String query,
        String work
) {
}
