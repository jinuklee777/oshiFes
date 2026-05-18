package com.oshifes.domain.ip.application;

import com.oshifes.domain.ip.application.dto.TranslatedName;

public interface CharacterNameTranslator {

    TranslatedName translate(String nativeName, String romajiName, String userPreferredName);
}
