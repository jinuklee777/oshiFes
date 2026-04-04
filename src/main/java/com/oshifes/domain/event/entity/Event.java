package com.oshifes.domain.event.entity;

import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
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
}
