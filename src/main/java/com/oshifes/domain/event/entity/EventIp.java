package com.oshifes.domain.event.entity;

import com.oshifes.domain.ip.entity.IpTitle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class EventIp {

    @EmbeddedId
    private EventIpId id;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @MapsId("ipTitleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_title_id", nullable = false)
    private IpTitle ipTitle;
}
