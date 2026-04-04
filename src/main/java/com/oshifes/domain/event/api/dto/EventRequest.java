package com.oshifes.domain.event.api.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record EventRequest(
        @NotBlank(message = "행사 제목은 필수입니다.") String title,
        String description,
        @NotBlank(message = "카테고리는 필수입니다.") String category,
        @NotBlank(message = "국가는 필수입니다.") String country,
        LocalDate startDate,
        LocalDate endDate,
        String venueName,
        String address,
        Double latitude,
        Double longitude,
        String imageUrl,
        String sourceUrl,
        @NotBlank(message = "출처 유형은 필수입니다.") String sourceType,
        String extra
) {
    @AssertTrue(message = "종료일은 시작일 이후여야 합니다.")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }
}
