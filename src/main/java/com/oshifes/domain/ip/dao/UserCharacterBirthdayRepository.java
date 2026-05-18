package com.oshifes.domain.ip.dao;

import com.oshifes.domain.ip.entity.UserCharacterBirthday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCharacterBirthdayRepository extends JpaRepository<UserCharacterBirthday, Long> {

    @EntityGraph(attributePaths = {"character", "character.ipTitle"})
    @Query("""
            select ucb
            from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.id = :characterId
            """)
    Optional<UserCharacterBirthday> findByUserIdAndCharacterId(@Param("userId") Long userId,
                                                               @Param("characterId") Long characterId);

    @EntityGraph(attributePaths = {"character", "character.ipTitle"})
    @Query("""
            select ucb
            from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.birthdayMonth is not null
              and ucb.character.birthdayDay is not null
              and (:month is null or ucb.character.birthdayMonth = :month)
              and (:day is null or ucb.character.birthdayDay = :day)
            """)
    Page<UserCharacterBirthday> searchBirthdays(@Param("userId") Long userId,
                                                @Param("month") Integer month,
                                                @Param("day") Integer day,
                                                Pageable pageable);

    @EntityGraph(attributePaths = {"character", "character.ipTitle"})
    @Query("""
            select ucb
            from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.birthdayMonth = :birthdayMonth
              and ucb.character.birthdayDay is not null
            """)
    List<UserCharacterBirthday> findByUserIdAndBirthdayMonth(@Param("userId") Long userId,
                                                             @Param("birthdayMonth") Integer birthdayMonth);

    @EntityGraph(attributePaths = {"character", "character.ipTitle"})
    @Query("""
            select ucb
            from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.birthdayMonth is not null
              and ucb.character.birthdayDay is not null
            """)
    List<UserCharacterBirthday> findAllWithBirthdayByUserId(@Param("userId") Long userId);

    long deleteByUserIdAndCharacterId(Long userId, Long characterId);
}
