package com.oshifes.domain.event.application;

import com.oshifes.domain.event.api.dto.EventRequest;
import com.oshifes.domain.event.api.dto.EventResponse;
import com.oshifes.domain.event.dao.EventRepository;
import com.oshifes.domain.event.entity.Event;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final EventRepository eventRepository;

    public List<EventResponse> getEvents() {
        return eventRepository.findAllByDeletedAtIsNull().stream()
                .map(EventResponse::from)
                .toList();
    }

    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Point location = buildPoint(request.getLatitude(), request.getLongitude());
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .country(request.getCountry())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .venueName(request.getVenueName())
                .address(request.getAddress())
                .location(location)
                .imageUrl(request.getImageUrl())
                .sourceUrl(request.getSourceUrl())
                .sourceType(request.getSourceType())
                .extra(request.getExtra())
                .build();
        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        Point location = buildPoint(request.getLatitude(), request.getLongitude());
        event.update(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getCountry(),
                request.getStartDate(),
                request.getEndDate(),
                request.getVenueName(),
                request.getAddress(),
                location,
                request.getImageUrl(),
                request.getSourceUrl(),
                request.getSourceType(),
                request.getExtra()
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
}
