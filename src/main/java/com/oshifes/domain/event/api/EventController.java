package com.oshifes.domain.event.api;

import com.oshifes.domain.event.api.dto.EventRequest;
import com.oshifes.domain.event.api.dto.EventResponse;
import com.oshifes.domain.event.api.dto.EventSearchCondition;
import com.oshifes.domain.event.application.EventService;
import com.oshifes.global.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@Validated
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<Page<EventResponse>> getEvents(
            @Pattern(regexp = "KR|JP", message = "country는 KR 또는 JP여야 합니다.")
            @RequestParam(required = false) String country,
            @Pattern(regexp = "^[a-z][a-z0-9_-]{0,30}$", message = "category는 영문 소문자, 숫자, -, _만 사용할 수 있습니다.")
            @RequestParam(required = false) String category,
            @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "month는 yyyy-MM 형식이어야 합니다.")
            @RequestParam(required = false) String month,
            @RequestParam(required = false) Long ipId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        EventSearchCondition condition = new EventSearchCondition(country, category, month, ipId);
        return ApiResponse.ok(eventService.getEvents(condition, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<EventResponse> getEvent(@PathVariable Long id) {
        return ApiResponse.ok(eventService.getEvent(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.createEvent(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EventResponse> updateEvent(@PathVariable Long id,
                                                   @Valid @RequestBody EventRequest request) {
        return ApiResponse.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ApiResponse.ok();
    }
}
