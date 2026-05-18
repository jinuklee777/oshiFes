package com.oshifes.domain.ip.entity;

import com.oshifes.domain.user.entity.User;
import com.oshifes.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
        name = "user_character_birthday",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_character_birthday_user_character",
                        columnNames = {"user_id", "character_id"}
                )
        }
)
public class UserCharacterBirthday extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @Builder
    private UserCharacterBirthday(User user, Character character) {
        this.user = user;
        this.character = character;
    }
}
