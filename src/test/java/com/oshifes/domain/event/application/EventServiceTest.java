package com.oshifes.domain.event.application;

import com.oshifes.domain.event.api.dto.EventRequest;
import com.oshifes.domain.event.api.dto.EventResponse;
import com.oshifes.domain.event.dao.EventRepository;
import com.oshifes.domain.event.entity.Event;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event createSampleEvent() {
        Point location = GEOMETRY_FACTORY.createPoint(new Coordinate(126.977, 37.5665));
        return Event.builder()
                .title("테스트 행사")
                .description("설명")
                .category("concert")
                .country("KR")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 31))
                .venueName("올림픽홀")
                .address("서울시 송파구")
                .location(location)
                .imageUrl("https://example.com/image.jpg")
                .sourceUrl("https://example.com")
                .sourceType("manual")
                .extra(null)
                .build();
    }

    @Test
    void getEvents_성공() {
        Pageable pageable = PageRequest.of(0, 20);
        given(eventRepository.findAllByDeletedAtIsNull(pageable))
                .willReturn(new PageImpl<>(List.of(createSampleEvent())));

        Page<EventResponse> result = eventService.getEvents(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 행사");
    }

    @Test
    void getEvent_성공() {
        given(eventRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(createSampleEvent()));

        EventResponse result = eventService.getEvent(1L);

        assertThat(result.getTitle()).isEqualTo("테스트 행사");
        assertThat(result.getCategory()).isEqualTo("concert");
    }

    @Test
    void getEvent_존재하지않는ID_예외() {
        given(eventRepository.findByIdAndDeletedAtIsNull(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEvent(99L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    void createEvent_성공() {
        EventRequest request = new EventRequest(
                "새 행사", "설명", "concert", "KR",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31),
                "올림픽홀", "서울", 37.5665, 126.977,
                null, null, "manual", null
        );
        given(eventRepository.save(any(Event.class))).willReturn(createSampleEvent());

        EventResponse result = eventService.createEvent(request);

        assertThat(result.getTitle()).isEqualTo("테스트 행사");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEvent_성공() {
        Event event = createSampleEvent();
        given(eventRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(event));
        EventRequest request = new EventRequest(
                "수정된 행사", "수정 설명", "festival", "JP",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30),
                "도쿄돔", "도쿄", 35.7056, 139.7519,
                null, null, "manual", null
        );

        EventResponse result = eventService.updateEvent(1L, request);

        assertThat(result.getTitle()).isEqualTo("수정된 행사");
        assertThat(result.getLatitude()).isEqualTo(35.7056);
        assertThat(result.getLongitude()).isEqualTo(139.7519);
    }

    @Test
    void updateEvent_위치정보없으면_기존위치유지() {
        Event event = createSampleEvent();
        given(eventRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(event));
        EventRequest request = new EventRequest(
                "수정된 행사", null, "concert", "KR",
                null, null, null, null,
                null, null, // latitude, longitude null
                null, null, "manual", null
        );

        EventResponse result = eventService.updateEvent(1L, request);

        assertThat(result.getLatitude()).isEqualTo(37.5665);
        assertThat(result.getLongitude()).isEqualTo(126.977);
    }

    @Test
    void updateEvent_존재하지않는ID_예외() {
        given(eventRepository.findByIdAndDeletedAtIsNull(99L)).willReturn(Optional.empty());
        EventRequest request = new EventRequest(
                "수정된 행사", null, "concert", "KR",
                null, null, null, null, null, null, null, null, "manual", null
        );

        assertThatThrownBy(() -> eventService.updateEvent(99L, request))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    void deleteEvent_성공() {
        Event event = createSampleEvent();
        given(eventRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(event));

        eventService.deleteEvent(1L);

        assertThat(event.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteEvent_존재하지않는ID_예외() {
        given(eventRepository.findByIdAndDeletedAtIsNull(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.deleteEvent(99L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.EVENT_NOT_FOUND);
    }
}
