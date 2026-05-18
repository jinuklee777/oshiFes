package com.oshifes.infrastructure.anilist;

public record AniListCharacterResult(
        String externalId,
        String nativeName,
        String fullName,
        String userPreferredName,
        Integer birthdayMonth,
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
