package com.oshifes.domain.ip.entity;

import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Character extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_title_id", nullable = false)
    private IpTitle ipTitle;

    @Column(nullable = false)
    private String nameKo;

    private String nameJa;

    private Integer birthdayMonth;

    private Integer birthdayDay;

    private String imageUrl;

    @Column(columnDefinition = "jsonb")
    private String extra;
}
