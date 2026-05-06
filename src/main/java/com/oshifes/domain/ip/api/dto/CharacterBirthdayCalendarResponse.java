package com.oshifes.domain.ip.api.dto;

import java.util.List;

public record CharacterBirthdayCalendarResponse(
        Integer month,
        Integer day,
        List<CharacterBirthdayResponse> birthdays
) {
}
