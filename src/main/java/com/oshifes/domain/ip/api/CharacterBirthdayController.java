package com.oshifes.domain.ip.api;

import com.oshifes.domain.ip.api.dto.CharacterBirthdayCalendarResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchResponse;
import com.oshifes.domain.ip.application.CharacterBirthdayService;
import com.oshifes.global.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/characters/birthdays")
@Validated
@RequiredArgsConstructor
public class CharacterBirthdayController {

    private final CharacterBirthdayService characterBirthdayService;

    @GetMapping
    public ApiResponse<Page<CharacterBirthdayResponse>> getBirthdays(
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @RequestParam(required = false) Integer month,
            @Min(value = 1, message = "day는 1 이상이어야 합니다.")
            @Max(value = 31, message = "day는 31 이하여야 합니다.")
            @RequestParam(required = false) Integer day,
            @PageableDefault(size = 20, sort = "birthdayMonth", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.ok(characterBirthdayService.getBirthdays(month, day, pageable));
    }

    @GetMapping("/calendar")
    public ApiResponse<List<CharacterBirthdayCalendarResponse>> getCalendar(
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @RequestParam Integer month) {
        return ApiResponse.ok(characterBirthdayService.getCalendar(month));
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<CharacterBirthdayResponse>> getUpcoming(
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            @Max(value = 50, message = "limit은 50 이하여야 합니다.")
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(characterBirthdayService.getUpcoming(limit));
    }

    @GetMapping("/search")
    public ApiResponse<CharacterBirthdaySearchResponse> search(
            @NotBlank(message = "query는 필수입니다.")
            @RequestParam String query,
            @RequestParam(required = false) String work,
            @RequestParam(defaultValue = "true") boolean includeExternal) {
        return ApiResponse.ok(characterBirthdayService.search(query, work, includeExternal));
    }

    @PostMapping("/anilist")
    public ApiResponse<CharacterBirthdayResponse> registerFromAniList(
            @Valid @RequestBody CharacterBirthdayRegisterRequest request) {
        return ApiResponse.ok(characterBirthdayService.registerFromAniList(request));
    }
}
