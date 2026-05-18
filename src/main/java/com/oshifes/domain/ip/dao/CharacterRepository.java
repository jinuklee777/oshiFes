package com.oshifes.domain.ip.dao;

import com.oshifes.domain.ip.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    @EntityGraph(attributePaths = "ipTitle")
    Page<Character> findByBirthdayMonth(Integer birthdayMonth, Pageable pageable);

    @EntityGraph(attributePaths = "ipTitle")
    Page<Character> findByBirthdayMonthAndBirthdayDay(Integer birthdayMonth, Integer birthdayDay, Pageable pageable);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.birthdayMonth is not null
              and c.birthdayDay is not null
              and (
                   (c.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and c.birthdayDay between 1 and 31)
                or (c.birthdayMonth in (4, 6, 9, 11) and c.birthdayDay between 1 and 30)
                or (c.birthdayMonth = 2 and c.birthdayDay between 1 and 29)
              )
              and (:month is null or c.birthdayMonth = :month)
              and (:day is null or c.birthdayDay = :day)
            """)
    Page<Character> searchBirthdays(@Param("month") Integer month,
                                    @Param("day") Integer day,
                                    Pageable pageable);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            join c.ipTitle t
            where c.birthdayMonth is not null
              and c.birthdayDay is not null
              and (
                   (c.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and c.birthdayDay between 1 and 31)
                or (c.birthdayMonth in (4, 6, 9, 11) and c.birthdayDay between 1 and 30)
                or (c.birthdayMonth = 2 and c.birthdayDay between 1 and 29)
              )
              and (
                   lower(c.nameKo) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(c.nameJa, '')) like lower(concat('%', :keyword, '%'))
                or lower(t.nameKo) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(t.nameJa, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(t.nameEn, '')) like lower(concat('%', :keyword, '%'))
              )
            """)
    List<Character> searchByKeyword(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.birthdayMonth = :birthdayMonth
              and c.birthdayDay is not null
              and (
                   (c.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and c.birthdayDay between 1 and 31)
                or (c.birthdayMonth in (4, 6, 9, 11) and c.birthdayDay between 1 and 30)
                or (c.birthdayMonth = 2 and c.birthdayDay between 1 and 29)
              )
            """)
    List<Character> findByBirthdayMonth(@Param("birthdayMonth") Integer birthdayMonth);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.birthdayMonth is not null
              and c.birthdayDay is not null
              and (
                   (c.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and c.birthdayDay between 1 and 31)
                or (c.birthdayMonth in (4, 6, 9, 11) and c.birthdayDay between 1 and 30)
                or (c.birthdayMonth = 2 and c.birthdayDay between 1 and 29)
              )
            """)
    List<Character> findAllWithBirthday();

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.birthdayMonth is not null
              and c.birthdayDay is not null
              and (
                   (c.birthdayMonth in (1, 3, 5, 7, 8, 10, 12) and c.birthdayDay between 1 and 31)
                or (c.birthdayMonth in (4, 6, 9, 11) and c.birthdayDay between 1 and 30)
                or (c.birthdayMonth = 2 and c.birthdayDay between 1 and 29)
              )
            order by
              case
                when c.birthdayMonth > :month
                  or (c.birthdayMonth = :month and c.birthdayDay >= :day)
                then 0
                else 1
              end,
              c.birthdayMonth,
              c.birthdayDay,
              c.nameKo
            """)
    List<Character> findUpcomingBirthdays(@Param("month") int month,
                                          @Param("day") int day,
                                          Pageable pageable);

    @EntityGraph(attributePaths = "ipTitle")
    Optional<Character> findBySourceTypeAndExternalId(String sourceType, String externalId);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.id = :id
            """)
    Optional<Character> findByIdWithIpTitle(@Param("id") Long id);
}
