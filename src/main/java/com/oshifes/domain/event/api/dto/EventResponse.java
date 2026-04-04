package com.oshifes.domain.event.api.dto;

import com.oshifes.domain.event.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String country;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venueName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String sourceUrl;
    private String externalId;
    private String sourceType;
    private boolean isAutoTranslated;
    private String extra;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventResponse from(Event event) {
        Double latitude = event.getLocation() != null ? event.getLocation().getY() : null;
        Double longitude = event.getLocation() != null ? event.getLocation().getX() : null;

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .country(event.getCountry())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .venueName(event.getVenueName())
                .address(event.getAddress())
                .latitude(latitude)
                .longitude(longitude)
                .imageUrl(event.getImageUrl())
                .sourceUrl(event.getSourceUrl())
                .externalId(event.getExternalId())
                .sourceType(event.getSourceType())
                .isAutoTranslated(event.isAutoTranslated())
                .extra(event.getExtra())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
