package com.oshifes.domain.event.type;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum EventCountry {
    KR, JP;

    private static final Set<String> NAMES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    public static boolean contains(String value) {
        return NAMES.contains(value);
    }
}
