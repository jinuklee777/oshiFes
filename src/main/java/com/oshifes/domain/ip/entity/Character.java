package com.oshifes.domain.ip.entity;

import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
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

    private String sourceType;

    private String externalId;

    private String sourceUrl;

    @Column(nullable = false)
    private boolean isAutoTranslated = false;

    @Column(columnDefinition = "jsonb")
    private String extra;

    @Builder
    private Character(IpTitle ipTitle, String nameKo, String nameJa, Integer birthdayMonth,
                      Integer birthdayDay, String imageUrl, String sourceType, String externalId,
                      String sourceUrl, boolean isAutoTranslated, String extra) {
        this.ipTitle = ipTitle;
        this.nameKo = nameKo;
        this.nameJa = nameJa;
        this.birthdayMonth = birthdayMonth;
        this.birthdayDay = birthdayDay;
        this.imageUrl = imageUrl;
        this.sourceType = sourceType;
        this.externalId = externalId;
        this.sourceUrl = sourceUrl;
        this.isAutoTranslated = isAutoTranslated;
        this.extra = extra;
    }
}
