package com.oshifes.infrastructure.anilist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestAniListClient implements AniListClient {

    private static final String ANILIST_URL = "https://graphql.anilist.co";
    private static final String CHARACTER_URL_PREFIX = "https://anilist.co/character/";
    private static final String QUERY = """
            query ($search: String) {
              Page(page: 1, perPage: 25) {
                characters(search: $search) {
                  id
                  name { full native userPreferred }
                  image { medium large }
                  dateOfBirth { month day }
                  media(perPage: 3, sort: POPULARITY_DESC) {
                    nodes {
                      id
                      title { romaji native userPreferred }
                    }
                  }
                }
              }
            }
            """;

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    @Override
    public List<AniListCharacterResult> searchCharacters(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        try {
            String response = restClient.post()
                    .uri(ANILIST_URL)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(Map.of("query", QUERY, "variables", Map.of("search", query)))
                    .retrieve()
                    .body(String.class);

            return parseResponse(response);
        } catch (RestClientException e) {
            log.warn("AniList character search failed. query={}", query, e);
            return List.of();
        }
    }

    private List<AniListCharacterResult> parseResponse(String response) {
        try {
            JsonNode characters = objectMapper.readTree(response)
                    .path("data")
                    .path("Page")
                    .path("characters");

            List<AniListCharacterResult> results = new ArrayList<>();
            if (!characters.isArray()) {
                return results;
            }

            for (JsonNode character : characters) {
                Integer month = nullableInt(character.path("dateOfBirth").path("month"));
                Integer day = nullableInt(character.path("dateOfBirth").path("day"));
                if (month == null || day == null) {
                    continue;
                }

                JsonNode media = firstMedia(character);
                String externalId = text(character.path("id"));
                results.add(new AniListCharacterResult(
                        externalId,
                        text(character.path("name").path("native")),
                        text(character.path("name").path("full")),
                        text(character.path("name").path("userPreferred")),
                        month,
                        day,
                        firstNonBlank(text(character.path("image").path("large")), text(character.path("image").path("medium"))),
                        text(media.path("id")),
                        text(media.path("title").path("native")),
                        text(media.path("title").path("romaji")),
                        text(media.path("title").path("userPreferred")),
                        externalId == null ? null : CHARACTER_URL_PREFIX + externalId,
                        character.toString()
                ));
            }
            return results;
        } catch (Exception e) {
            log.warn("AniList response parsing failed.", e);
            return List.of();
        }
    }

    private JsonNode firstMedia(JsonNode character) {
        JsonNode nodes = character.path("media").path("nodes");
        if (nodes.isArray() && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return objectMapper.createObjectNode();
    }

    private Integer nullableInt(JsonNode node) {
        return node.isInt() ? node.asInt() : null;
    }

    private String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return value.isBlank() ? null : value;
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
