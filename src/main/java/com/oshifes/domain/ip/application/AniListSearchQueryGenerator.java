package com.oshifes.domain.ip.application;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class AniListSearchQueryGenerator {

    private static final String[] CHO = {
            "g", "kk", "n", "d", "tt", "r", "m", "b", "pp", "s", "ss", "",
            "j", "jj", "ch", "k", "t", "p", "h"
    };
    private static final String[] JUNG = {
            "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe",
            "yo", "u", "wo", "we", "wi", "yu", "eu", "ui", "i"
    };
    private static final String[] JONG = {
            "", "g", "kk", "gs", "n", "nj", "nh", "d", "l", "lg", "lm", "lb", "ls",
            "lt", "lp", "lh", "m", "b", "bs", "s", "ss", "ng", "j", "ch", "k", "t", "p", "h"
    };
    private static final String[] JP_CHO = {
            "g", "k", "n", "d", "t", "r", "m", "b", "p", "s", "s", "",
            "z", "j", "ch", "k", "t", "p", "h"
    };
    private static final String[] JP_JUNG = {
            "a", "e", "ya", "ye", "o", "e", "yo", "ye", "o", "wa", "we", "oe",
            "yo", "u", "wo", "we", "wi", "yu", "u", "ui", "i"
    };

    public List<String> generate(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String trimmed = query.trim();
        Set<String> variants = new LinkedHashSet<>();
        variants.add(trimmed);

        if (hasKorean(trimmed)) {
            String romaji = toRomaji(trimmed);
            String japaneseRomaji = toJapaneseRomaji(trimmed);
            String reversed = reverseWords(trimmed);
            variants.add(toJapaneseRomaji(reversed));
            variants.add(japaneseRomaji);
            variants.add(reversed);
            variants.add(romaji);
            variants.add(toRomaji(reversed));
        }

        return variants.stream()
                .filter(value -> !value.isBlank())
                .toList();
    }

    public List<String> splitFallbacks(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        Set<String> parts = new LinkedHashSet<>();
        for (String part : query.trim().split("\\s+")) {
            if (part.length() >= 2) {
                parts.add(part);
            }
        }

        if (hasKorean(query)) {
            for (String part : toJapaneseRomaji(query).split("\\s+")) {
                if (part.length() >= 2) {
                    parts.add(part);
                }
            }
            for (String part : toRomaji(query).split("\\s+")) {
                if (part.length() >= 2) {
                    parts.add(part);
                }
            }
        }

        return new ArrayList<>(parts);
    }

    private boolean hasKorean(String value) {
        return value.chars().anyMatch(ch -> ch >= 0xAC00 && ch <= 0xD7A3);
    }

    private String reverseWords(String value) {
        String[] parts = value.trim().split("\\s+");
        List<String> reversed = new ArrayList<>();
        for (int i = parts.length - 1; i >= 0; i--) {
            reversed.add(parts[i]);
        }
        return String.join(" ", reversed);
    }

    private String toRomaji(String value) {
        List<String> words = new ArrayList<>();
        for (String word : value.trim().split("\\s+")) {
            words.add(syllablesToRomaji(word));
        }
        return String.join(" ", words);
    }

    private String syllablesToRomaji(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            int code = ch - 0xAC00;
            if (code < 0 || code > 11171) {
                result.append(ch);
                continue;
            }

            int cho = code / 588;
            int jung = (code % 588) / 28;
            int jong = code % 28;
            result.append(CHO[cho]).append(JUNG[jung]).append(JONG[jong]);
        }
        return result.toString();
    }

    private String toJapaneseRomaji(String value) {
        List<String> words = new ArrayList<>();
        for (String word : value.trim().split("\\s+")) {
            words.add(syllablesToJapaneseRomaji(word));
        }
        return String.join(" ", words);
    }

    private String syllablesToJapaneseRomaji(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            int code = ch - 0xAC00;
            if (code < 0 || code > 11171) {
                result.append(ch);
                continue;
            }

            int cho = code / 588;
            int jung = (code % 588) / 28;
            int jong = code % 28;
            result.append(japaneseInitial(cho, jung)).append(JP_JUNG[jung]).append(JONG[jong]);
        }
        return result.toString();
    }

    private String japaneseInitial(int cho, int jung) {
        if (cho == 12 && jung == 20) {
            return "j";
        }
        if (cho == 14 && jung == 20) {
            return "ch";
        }
        if (cho == 9 && jung == 20) {
            return "sh";
        }
        return JP_CHO[cho];
    }
}
