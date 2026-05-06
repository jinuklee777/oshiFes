package com.oshifes.domain.ip.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CharacterBirthdayRegisterRequest(
        @NotBlank(message = "캐릭터 이름은 필수입니다.") String nameKo,
        @NotBlank(message = "AniList 캐릭터 ID는 필수입니다.") String externalId,
        String nativeName,
        String fullName,
        String userPreferredName,
        @NotNull(message = "생일 월은 필수입니다.")
        @Min(value = 1, message = "birthdayMonth는 1 이상이어야 합니다.")
        @Max(value = 12, message = "birthdayMonth는 12 이하여야 합니다.")
        Integer birthdayMonth,
        @NotNull(message = "생일 일은 필수입니다.")
        @Min(value = 1, message = "birthdayDay는 1 이상이어야 합니다.")
        @Max(value = 31, message = "birthdayDay는 31 이하여야 합니다.")
        Integer birthdayDay,
        String imageUrl,
        String mediaExternalId,
        String mediaNativeTitle,
        String mediaRomajiTitle,
        String mediaUserPreferredTitle,
        String sourceUrl,
        String rawJson
) {
}
