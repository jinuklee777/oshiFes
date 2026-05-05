package com.oshifes.domain.event.api.dto;

public record EventSearchCondition(
        String country,
        String category,
        String month,
        Long ipId
) {
}
