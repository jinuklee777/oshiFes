package com.oshifes.domain.event.application.dto;

public record EventSearchCondition(
        String country,
        String category,
        String month,
        Long ipId
) {
}
