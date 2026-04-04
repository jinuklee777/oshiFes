package com.oshifes.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class EventIpId implements Serializable {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "ip_title_id")
    private Long ipTitleId;

    public EventIpId(Long eventId, Long ipTitleId) {
        this.eventId = eventId;
        this.ipTitleId = ipTitleId;
    }
}
