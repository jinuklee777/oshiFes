package com.oshifes.domain.ip.dao;

import com.oshifes.domain.ip.entity.UserCharacterBirthday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
              and (
                   (ucb.character.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and ucb.character.birthdayDay between 1 and 31)
                or (ucb.character.birthdayMonth in (4, 6, 9, 11) and ucb.character.birthdayDay between 1 and 30)
                or (ucb.character.birthdayMonth = 2 and ucb.character.birthdayDay between 1 and 29)
              )
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
              and (
                   (ucb.character.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and ucb.character.birthdayDay between 1 and 31)
                or (ucb.character.birthdayMonth in (4, 6, 9, 11) and ucb.character.birthdayDay between 1 and 30)
                or (ucb.character.birthdayMonth = 2 and ucb.character.birthdayDay between 1 and 29)
              )
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
              and (
                   (ucb.character.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and ucb.character.birthdayDay between 1 and 31)
                or (ucb.character.birthdayMonth in (4, 6, 9, 11) and ucb.character.birthdayDay between 1 and 30)
                or (ucb.character.birthdayMonth = 2 and ucb.character.birthdayDay between 1 and 29)
              )
            """)
    List<UserCharacterBirthday> findAllWithBirthdayByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"character", "character.ipTitle"})
    @Query("""
            select ucb
            from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.birthdayMonth is not null
              and ucb.character.birthdayDay is not null
              and (
                   (ucb.character.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and ucb.character.birthdayDay between 1 and 31)
                or (ucb.character.birthdayMonth in (4, 6, 9, 11) and ucb.character.birthdayDay between 1 and 30)
                or (ucb.character.birthdayMonth = 2 and ucb.character.birthdayDay between 1 and 29)
              )
            order by
              case
                when ucb.character.birthdayMonth > :month
                  or (ucb.character.birthdayMonth = :month and ucb.character.birthdayDay >= :day)
                then 0
                else 1
              end,
              ucb.character.birthdayMonth,
              ucb.character.birthdayDay,
              ucb.character.nameKo
            """)
    List<UserCharacterBirthday> findUpcomingBirthdaysByUserId(@Param("userId") Long userId,
                                                              @Param("month") int month,
                                                              @Param("day") int day,
                                                              Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from UserCharacterBirthday ucb
            where ucb.user.id = :userId
              and ucb.character.id = :characterId
            """)
    long deleteByUserIdAndCharacterId(@Param("userId") Long userId,
                                      @Param("characterId") Long characterId);
}
