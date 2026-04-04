package com.oshifes.domain.event.entity;

import com.oshifes.domain.user.entity.User;
import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class EventReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String category;

    private String country;

    private LocalDate startDate;

    private LocalDate endDate;

    private String venueName;

    private String imageUrl;

    private String sourceUrl;

    @Column(nullable = false)
    private String status;

    private Long approvedEventId;
}
