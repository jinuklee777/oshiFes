package com.oshifes.domain.ip.entity;

import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class IpTitle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameKo;

    private String nameJa;

    private String nameEn;

    @Column(nullable = false)
    private String category;

    private String thumbnailUrl;

    private String sourceType;

    private String externalId;

    private String sourceUrl;

    @Column(nullable = false)
    private boolean isAutoTranslated = false;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String extra;

    @Builder
    private IpTitle(String nameKo, String nameJa, String nameEn, String category, String thumbnailUrl,
                    String sourceType, String externalId, String sourceUrl, boolean isAutoTranslated, String extra) {
        this.nameKo = nameKo;
        this.nameJa = nameJa;
        this.nameEn = nameEn;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.sourceType = sourceType;
        this.externalId = externalId;
        this.sourceUrl = sourceUrl;
        this.isAutoTranslated = isAutoTranslated;
        this.extra = extra;
    }
}
