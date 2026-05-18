package com.oshifes.domain.ip.api.dto;

import java.util.List;

public record CharacterBirthdaySearchResponse(
        List<CharacterBirthdayResponse> registered,
        List<AniListCharacterCandidateResponse> candidates
) {
}
