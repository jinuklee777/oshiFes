package com.oshifes.domain.event.type;

import java.util.Arrays;

public enum EventCountry {
    KR, JP;

    public static boolean contains(String value) {
        return Arrays.stream(values())
                .anyMatch(country -> country.name().equals(value));
    }
}
