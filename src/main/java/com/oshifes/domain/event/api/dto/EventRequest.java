package com.oshifes.domain.event.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventRequest {

    @NotBlank(message = "행사 제목은 필수입니다.")
    private String title;

    private String description;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @NotBlank(message = "국가는 필수입니다.")
    private String country;

    private LocalDate startDate;

    private LocalDate endDate;

    private String venueName;

    private String address;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private String sourceUrl;

    @NotBlank(message = "출처 유형은 필수입니다.")
    private String sourceType;

    private String extra;
}
