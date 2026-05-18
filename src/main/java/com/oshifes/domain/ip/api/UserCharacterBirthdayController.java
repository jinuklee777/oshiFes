package com.oshifes.domain.ip.api;

import com.oshifes.domain.ip.api.dto.CharacterBirthdayCalendarResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddResponse;
import com.oshifes.domain.ip.application.CharacterBirthdayService;
import com.oshifes.global.common.ApiResponse;
import com.oshifes.global.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/characters/birthdays")
@Validated
@RequiredArgsConstructor
public class UserCharacterBirthdayController {

    private final CharacterBirthdayService characterBirthdayService;

    @GetMapping
    public ApiResponse<Page<CharacterBirthdayResponse>> getMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @RequestParam(required = false) Integer month,
            @Min(value = 1, message = "day는 1 이상이어야 합니다.")
            @Max(value = 31, message = "day는 31 이하여야 합니다.")
            @RequestParam(required = false) Integer day,
            @Min(value = 0, message = "page는 0 이상이어야 합니다.")
            @RequestParam(defaultValue = "0") int page,
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하여야 합니다.")
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(characterBirthdayService.getMyBirthdays(principal.getUserId(), month, day, pageRequest));
    }

    @GetMapping("/calendar")
    public ApiResponse<List<CharacterBirthdayCalendarResponse>> getMyCalendar(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @RequestParam Integer month) {
        return ApiResponse.ok(characterBirthdayService.getMyCalendar(principal.getUserId(), month));
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<CharacterBirthdayResponse>> getMyUpcoming(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            @Max(value = 50, message = "limit은 50 이하여야 합니다.")
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(characterBirthdayService.getMyUpcoming(principal.getUserId(), limit));
    }

    @PostMapping("/{characterId}")
    public ApiResponse<CharacterBirthdayResponse> addToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long characterId) {
        return ApiResponse.ok(characterBirthdayService.addToMyBirthdays(principal.getUserId(), characterId));
    }

    @PostMapping("/anilist")
    public ApiResponse<CharacterBirthdayResponse> registerFromAniListToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CharacterBirthdayRegisterRequest request) {
        return ApiResponse.ok(characterBirthdayService.registerFromAniListToMyBirthdays(principal.getUserId(), request));
    }

    @PostMapping("/search")
    public ApiResponse<CharacterBirthdaySearchAddResponse> searchAndAddToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CharacterBirthdaySearchAddRequest request) {
        return ApiResponse.ok(characterBirthdayService.searchAndAddToMyBirthdays(principal.getUserId(), request));
    }

    @DeleteMapping("/{characterId}")
    public ApiResponse<Void> removeFromMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long characterId) {
        characterBirthdayService.removeFromMyBirthdays(principal.getUserId(), characterId);
        return ApiResponse.ok();
    }
}
