package com.oshifes.domain.ip.application.dto;

public record TranslatedName(
        String nameKo,
        String nameJa,
        String nameEn,
        boolean autoTranslated
) {
}
