package com.oshifes.domain.ip.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oshifes.domain.ip.entity.Character;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CharacterBirthdayResponse {

    private Long characterId;
    private String nameKo;
    private String nameJa;
    private Integer birthdayMonth;
    private Integer birthdayDay;
    private String imageUrl;
    private Long ipTitleId;
    private String ipTitleNameKo;
    private String ipTitleNameJa;
    private String ipTitleNameEn;
    private String sourceType;
    private String externalId;
    @JsonProperty("isAutoTranslated")
    private boolean isAutoTranslated;
    private int daysUntilBirthday;

    public static CharacterBirthdayResponse from(Character character, int daysUntilBirthday) {
        return CharacterBirthdayResponse.builder()
                .characterId(character.getId())
                .nameKo(character.getNameKo())
                .nameJa(character.getNameJa())
                .birthdayMonth(character.getBirthdayMonth())
                .birthdayDay(character.getBirthdayDay())
                .imageUrl(character.getImageUrl())
                .ipTitleId(character.getIpTitle().getId())
                .ipTitleNameKo(character.getIpTitle().getNameKo())
                .ipTitleNameJa(character.getIpTitle().getNameJa())
                .ipTitleNameEn(character.getIpTitle().getNameEn())
                .sourceType(character.getSourceType())
                .externalId(character.getExternalId())
                .isAutoTranslated(character.isAutoTranslated())
                .daysUntilBirthday(daysUntilBirthday)
                .build();
    }
}
