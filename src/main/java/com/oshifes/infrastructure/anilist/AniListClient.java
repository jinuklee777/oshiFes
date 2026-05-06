package com.oshifes.infrastructure.anilist;

import java.util.List;

public interface AniListClient {

    List<AniListCharacterResult> searchCharacters(String query);
}
