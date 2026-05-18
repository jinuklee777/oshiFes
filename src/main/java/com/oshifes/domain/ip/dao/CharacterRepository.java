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
            """)
    List<Character> findByBirthdayMonth(@Param("birthdayMonth") Integer birthdayMonth);

    @EntityGraph(attributePaths = "ipTitle")
    @Query("""
            select c
            from Character c
            where c.birthdayMonth is not null
              and c.birthdayDay is not null
            """)
    List<Character> findAllWithBirthday();

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
