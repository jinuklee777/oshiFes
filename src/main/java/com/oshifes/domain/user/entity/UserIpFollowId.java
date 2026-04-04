package com.oshifes.domain.user.entity;

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
public class UserIpFollowId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ip_title_id")
    private Long ipTitleId;

    public UserIpFollowId(Long userId, Long ipTitleId) {
        this.userId = userId;
        this.ipTitleId = ipTitleId;
    }
}
