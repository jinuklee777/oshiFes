package com.oshifes.domain.event.application;

import com.oshifes.domain.event.api.dto.EventRequest;
import com.oshifes.domain.event.api.dto.EventResponse;
import com.oshifes.domain.event.api.dto.EventSearchCondition;
import com.oshifes.domain.event.dao.EventRepository;
import com.oshifes.domain.event.entity.Event;
import com.oshifes.domain.event.entity.EventIp;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final EventRepository eventRepository;

    public Page<EventResponse> getEvents(EventSearchCondition condition, Pageable pageable) {
        return eventRepository.findAll(buildSearchSpecification(condition), pageable)
                .map(EventResponse::from);
    }

    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Point location = buildPoint(request.latitude(), request.longitude());
        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .country(request.country())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .venueName(request.venueName())
                .address(request.address())
                .location(location)
                .imageUrl(request.imageUrl())
                .sourceUrl(request.sourceUrl())
                .sourceType(request.sourceType())
                .extra(request.extra())
                .build();
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        Point location = (request.latitude() != null && request.longitude() != null)
                ? buildPoint(request.latitude(), request.longitude())
                : event.getLocation();
        event.update(
                request.title(),
                request.description(),
                request.category(),
                request.country(),
                request.startDate(),
                request.endDate(),
                request.venueName(),
                request.address(),
                location,
                request.imageUrl(),
                request.sourceUrl(),
                request.sourceType(),
                request.extra()
        );
        return EventResponse.from(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        event.delete();
    }

    private Point buildPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    private Specification<Event> buildSearchSpecification(EventSearchCondition condition) {
        YearMonth yearMonth = StringUtils.hasText(condition.month()) ? parseYearMonth(condition.month()) : null;

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (StringUtils.hasText(condition.country())) {
                predicates.add(criteriaBuilder.equal(root.get("country"), condition.country().trim()));
            }

            if (StringUtils.hasText(condition.category())) {
                predicates.add(criteriaBuilder.equal(root.get("category"), condition.category().trim()));
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

            if (condition.ipId() != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                var eventIp = subquery.from(EventIp.class);
                subquery.select(eventIp.get("event").get("id"))
                        .where(
                                criteriaBuilder.equal(eventIp.get("event"), root),
                                criteriaBuilder.equal(eventIp.get("ipTitle").get("id"), condition.ipId())
                        );
                predicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private YearMonth parseYearMonth(String month) {
        try {
            return YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "month는 yyyy-MM 형식이어야 합니다.");
        }
    }
}
