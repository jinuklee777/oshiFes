package com.oshifes.domain.ip.api.dto;

import java.util.List;

public record CharacterBirthdaySearchAddResponse(
        CharacterBirthdayResponse added,
        List<AniListCharacterCandidateResponse> candidates
) {
    public static CharacterBirthdaySearchAddResponse added(CharacterBirthdayResponse added) {
        return new CharacterBirthdaySearchAddResponse(added, List.of());
    }

    public static CharacterBirthdaySearchAddResponse candidates(List<AniListCharacterCandidateResponse> candidates) {
        return new CharacterBirthdaySearchAddResponse(null, candidates);
    }
}
