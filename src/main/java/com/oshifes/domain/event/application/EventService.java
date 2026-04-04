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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final EventRepository eventRepository;

    public Page<EventResponse> getEvents(Pageable pageable) {
        return eventRepository.findAllByDeletedAtIsNull(pageable)
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
}
