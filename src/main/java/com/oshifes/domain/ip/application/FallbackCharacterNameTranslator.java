package com.oshifes.domain.ip.application;

import com.oshifes.domain.ip.application.dto.TranslatedName;
import org.springframework.stereotype.Component;

@Component
public class FallbackCharacterNameTranslator implements CharacterNameTranslator {

    @Override
    public TranslatedName translate(String nativeName, String romajiName, String userPreferredName) {
        String nameKo = firstNonBlank(nativeName, userPreferredName, romajiName, "Unknown");
        String nameEn = firstNonBlank(romajiName, userPreferredName, null);
        return new TranslatedName(nameKo, nativeName, nameEn, true);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
