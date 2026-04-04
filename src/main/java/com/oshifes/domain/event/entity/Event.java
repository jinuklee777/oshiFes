package com.oshifes.domain.event.entity;

import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Event extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String country;

    private LocalDate startDate;

    private LocalDate endDate;

    private String venueName;

    private String address;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    private String imageUrl;

    private String sourceUrl;

    @Column(unique = true)
    private String externalId;

    @Column(nullable = false)
    private String sourceType;

    @Column(nullable = false)
    private boolean isAutoTranslated = false;

    @Column(columnDefinition = "jsonb")
    private String extra;

    private LocalDateTime deletedAt;

    @Builder
    private Event(String title, String description, String category, String country,
                  LocalDate startDate, LocalDate endDate, String venueName, String address,
                  Point location, String imageUrl, String sourceUrl, String externalId,
                  String sourceType, boolean isAutoTranslated, String extra) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venueName = venueName;
        this.address = address;
        this.location = location;
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
        this.externalId = externalId;
        this.sourceType = sourceType;
        this.isAutoTranslated = isAutoTranslated;
        this.extra = extra;
    }

    public void update(String title, String description, String category, String country,
                       LocalDate startDate, LocalDate endDate, String venueName, String address,
                       Point location, String imageUrl, String sourceUrl, String sourceType, String extra) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.country = country;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venueName = venueName;
        this.address = address;
        this.location = location;
        this.imageUrl = imageUrl;
        this.sourceUrl = sourceUrl;
        this.sourceType = sourceType;
        this.extra = extra;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
