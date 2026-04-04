package com.oshifes.domain.user.entity;

import com.oshifes.domain.ip.entity.IpTitle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class UserIpFollow {

    @EmbeddedId
    private UserIpFollowId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("ipTitleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_title_id", nullable = false)
    private IpTitle ipTitle;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
