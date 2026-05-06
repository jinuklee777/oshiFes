package com.oshifes.domain.ip.api.dto;

import com.oshifes.infrastructure.anilist.AniListCharacterResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AniListCharacterCandidateResponse {

    private String externalId;
    private String nativeName;
    private String fullName;
    private String userPreferredName;
    private Integer birthdayMonth;
    private Integer birthdayDay;
    private String imageUrl;
    private String mediaExternalId;
    private String mediaNativeTitle;
    private String mediaRomajiTitle;
    private String mediaUserPreferredTitle;
    private String sourceUrl;
    private String rawJson;

    public static AniListCharacterCandidateResponse from(AniListCharacterResult result) {
        return AniListCharacterCandidateResponse.builder()
                .externalId(result.externalId())
                .nativeName(result.nativeName())
                .fullName(result.fullName())
                .userPreferredName(result.userPreferredName())
                .birthdayMonth(result.birthdayMonth())
                .birthdayDay(result.birthdayDay())
                .imageUrl(result.imageUrl())
                .mediaExternalId(result.mediaExternalId())
                .mediaNativeTitle(result.mediaNativeTitle())
                .mediaRomajiTitle(result.mediaRomajiTitle())
                .mediaUserPreferredTitle(result.mediaUserPreferredTitle())
                .sourceUrl(result.sourceUrl())
                .rawJson(result.rawJson())
                .build();
    }
}
