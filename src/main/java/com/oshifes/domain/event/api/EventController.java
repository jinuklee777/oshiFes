package com.oshifes.domain.event.api;

import com.oshifes.domain.event.api.dto.EventRequest;
import com.oshifes.domain.event.api.dto.EventResponse;
import com.oshifes.domain.event.application.EventService;
import com.oshifes.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<List<EventResponse>> getEvents() {
        return ApiResponse.ok(eventService.getEvents());
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long id) {
        return ApiResponse.ok(eventService.getEvent(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.createEvent(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<EventResponse> updateEvent(@PathVariable Long id,
                                                   @Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ApiResponse.ok();
    }
}
