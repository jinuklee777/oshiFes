package com.oshifes.domain.pilgrimage.entity;

import com.oshifes.domain.ip.entity.IpTitle;
import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class PilgrimageSpot extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_title_id", nullable = false)
    private IpTitle ipTitle;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String spotType;

    private String address;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point location;

    private String imageUrl;
}
