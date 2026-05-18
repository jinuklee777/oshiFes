package com.oshifes.domain.ip.api;

import com.oshifes.domain.ip.api.dto.CharacterBirthdayCalendarResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddResponse;
import com.oshifes.domain.ip.application.CharacterBirthdayService;
import com.oshifes.global.common.ApiResponse;
import com.oshifes.global.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "내 캐릭터 생일 목록 조회", description = "로그인 사용자가 담아둔 캐릭터 생일 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CharacterBirthdayResponse>>> getMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @Parameter(description = "조회할 생일 월", example = "11")
            @RequestParam(required = false) Integer month,
            @Min(value = 1, message = "day는 1 이상이어야 합니다.")
            @Max(value = 31, message = "day는 31 이하여야 합니다.")
            @Parameter(description = "조회할 생일 일", example = "11")
            @RequestParam(required = false) Integer day,
            @Min(value = 0, message = "page는 0 이상이어야 합니다.")
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하여야 합니다.")
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(
                ApiResponse.ok(characterBirthdayService.getMyBirthdays(principal.getUserId(), month, day, pageRequest))
        );
    }

    @Operation(summary = "내 캐릭터 생일 캘린더 조회", description = "내 생일 목록을 특정 월 기준 일자별로 그룹화해 조회합니다.")
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<CharacterBirthdayCalendarResponse>>> getMyCalendar(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "month는 1 이상이어야 합니다.")
            @Max(value = 12, message = "month는 12 이하여야 합니다.")
            @Parameter(description = "조회할 생일 월", example = "11")
            @RequestParam Integer month) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.getMyCalendar(principal.getUserId(), month)));
    }

    @Operation(summary = "다가오는 내 캐릭터 생일 조회", description = "내 목록에서 오늘 기준 가까운 캐릭터 생일을 조회합니다.")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<CharacterBirthdayResponse>>> getMyUpcoming(
            @AuthenticationPrincipal UserPrincipal principal,
            @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
            @Max(value = 50, message = "limit은 50 이하여야 합니다.")
            @Parameter(description = "조회 개수", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(characterBirthdayService.getMyUpcoming(principal.getUserId(), limit)));
    }

    @Operation(summary = "전체 DB 캐릭터를 내 생일 목록에 추가", description = "공용 캐릭터 생일 DB의 캐릭터를 내 목록에 추가합니다.")
    @PostMapping("/{characterId}")
    public ResponseEntity<ApiResponse<CharacterBirthdayResponse>> addToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "추가할 캐릭터 ID", example = "1")
            @PathVariable Long characterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(characterBirthdayService.addToMyBirthdays(principal.getUserId(), characterId)));
    }

    @Operation(summary = "AniList 캐릭터를 등록하고 내 생일 목록에 추가", description = "공용 DB에 없으면 먼저 등록한 뒤 내 목록에 추가합니다.")
    @PostMapping("/anilist")
    public ResponseEntity<ApiResponse<CharacterBirthdayResponse>> registerFromAniListToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CharacterBirthdayRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        characterBirthdayService.registerFromAniListToMyBirthdays(principal.getUserId(), request)
                ));
    }

    @Operation(summary = "검색 후 내 생일 목록에 추가", description = "DB에 검색 결과가 있으면 첫 번째 캐릭터를 내 목록에 추가하고, 없으면 AniList 후보를 반환합니다.")
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<CharacterBirthdaySearchAddResponse>> searchAndAddToMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CharacterBirthdaySearchAddRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(characterBirthdayService.searchAndAddToMyBirthdays(principal.getUserId(), request))
        );
    }

    @Operation(summary = "내 캐릭터 생일 목록에서 제거", description = "내 목록에서 지정한 캐릭터를 제거합니다.")
    @DeleteMapping("/{characterId}")
    public ResponseEntity<ApiResponse<Void>> removeFromMyBirthdays(
            @AuthenticationPrincipal UserPrincipal principal,
            @Parameter(description = "제거할 캐릭터 ID", example = "1")
            @PathVariable Long characterId) {
        characterBirthdayService.removeFromMyBirthdays(principal.getUserId(), characterId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
