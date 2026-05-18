package com.oshifes.domain.ip.api;

import com.oshifes.domain.ip.api.dto.CharacterBirthdayCalendarResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchResponse;
import com.oshifes.domain.ip.application.CharacterBirthdayService;
import com.oshifes.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "전체 캐릭터 생일 목록 조회", description = "공용 캐릭터 생일 DB를 월/일 조건으로 조회합니다.")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CharacterBirthdayResponse>>> getBirthdays(
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @Parameter(description = "조회할 생일 월", example = "11")
            @RequestParam(required = false) Integer month,
            @Min(value = 1, message = "day는 1 이상이어야 합니다.")
            @Max(value = 31, message = "day는 31 이하여야 합니다.")
            @Parameter(description = "조회할 생일 일", example = "11")
            @RequestParam(required = false) Integer day,
            @PageableDefault(size = 20, sort = "birthdayMonth", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.getBirthdays(month, day, pageable)));
    }

    @Operation(summary = "전체 캐릭터 생일 캘린더 조회", description = "공용 캐릭터 생일 DB를 특정 월 기준 일자별로 그룹화해 조회합니다.")
    @SecurityRequirements
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<CharacterBirthdayCalendarResponse>>> getCalendar(
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @Parameter(description = "조회할 생일 월", example = "11")
            @RequestParam Integer month) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.getCalendar(month)));
    }

    @Operation(summary = "다가오는 전체 캐릭터 생일 조회", description = "오늘 기준 가까운 캐릭터 생일을 조회합니다.")
    @SecurityRequirements
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<CharacterBirthdayResponse>>> getUpcoming(
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            @Max(value = 50, message = "limit은 50 이하여야 합니다.")
            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.getUpcoming(limit)));
    }

    @Operation(summary = "캐릭터 생일 검색", description = "먼저 공용 DB에서 검색하고, 결과가 없으면 AniList 후보를 조회할 수 있습니다.")
    @SecurityRequirements
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CharacterBirthdaySearchResponse>> search(
            @NotBlank(message = "query는 필수입니다.")
            @Parameter(description = "캐릭터명 또는 작품명 검색어", example = "아즈사")
            @RequestParam String query,
            @Parameter(description = "AniList 후보 필터링용 작품명", example = "K-On")
            @RequestParam(required = false) String work,
            @Parameter(description = "DB 결과가 없을 때 AniList 후보를 포함할지 여부", example = "true")
            @RequestParam(defaultValue = "true") boolean includeExternal) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.search(query, work, includeExternal)));
    }

    @Operation(summary = "AniList 캐릭터를 전체 생일 DB에 등록", description = "AniList 후보를 선택해 공용 캐릭터 생일 DB에 등록합니다.")
    @PostMapping("/anilist")
    public ResponseEntity<ApiResponse<CharacterBirthdayResponse>> registerFromAniList(
            @Valid @RequestBody CharacterBirthdayRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(characterBirthdayService.registerFromAniList(request)));
    }
}
