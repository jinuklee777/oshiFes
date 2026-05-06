package com.oshifes.domain.event.dao;

import com.oshifes.domain.event.entity.Event;
import com.oshifes.domain.event.entity.EventIp;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class EventSpecifications {

    private EventSpecifications() {
    }

    public static Specification<Event> withCondition(String country, String category, String month, Long ipId) {
        YearMonth yearMonth = StringUtils.hasText(month) ? parseYearMonth(month) : null;

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (StringUtils.hasText(country)) {
                predicates.add(criteriaBuilder.equal(root.get("country"), country.trim()));
            }

            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category.trim()));
            }

            if (yearMonth != null) {
                LocalDate firstDay = yearMonth.atDay(1);
                LocalDate lastDay = yearMonth.atEndOfMonth();

                predicates.add(criteriaBuilder.isNotNull(root.get("startDate")));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), lastDay));
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), firstDay),
                        criteriaBuilder.and(
                                criteriaBuilder.isNull(root.get("endDate")),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), firstDay)
                        )
                ));
            }

            if (ipId != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<EventIp> eventIp = subquery.from(EventIp.class);
                subquery.select(criteriaBuilder.literal(1L))
                        .where(
                                criteriaBuilder.equal(eventIp.get("event"), root),
                                criteriaBuilder.equal(eventIp.get("ipTitle").get("id"), ipId)
                        );
                predicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static YearMonth parseYearMonth(String month) {
        try {
            // Controller validation catches user input first; this guards direct service calls.
            return YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "month는 yyyy-MM 형식의 유효한 연-월이어야 합니다.");
        }
    }
}
