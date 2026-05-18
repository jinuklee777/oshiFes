package com.oshifes.domain.ip.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oshifes.domain.ip.api.dto.AniListCharacterCandidateResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayCalendarResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchAddResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchResponse;
import com.oshifes.domain.ip.application.dto.TranslatedName;
import com.oshifes.domain.ip.dao.CharacterRepository;
import com.oshifes.domain.ip.dao.IpTitleRepository;
import com.oshifes.domain.ip.dao.UserCharacterBirthdayRepository;
import com.oshifes.domain.ip.entity.Character;
import com.oshifes.domain.ip.entity.IpTitle;
import com.oshifes.domain.ip.entity.UserCharacterBirthday;
import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.global.error.CustomException;
import com.oshifes.global.error.ErrorCode;
import com.oshifes.infrastructure.anilist.AniListCharacterResult;
import com.oshifes.infrastructure.anilist.AniListClient;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CharacterBirthdayService {

    private static final String SOURCE_TYPE_ANILIST = "ANILIST";
    private static final String DEFAULT_CATEGORY = "anime";

    private final CharacterRepository characterRepository;
    private final IpTitleRepository ipTitleRepository;
    private final UserCharacterBirthdayRepository userCharacterBirthdayRepository;
    private final UserRepository userRepository;
    private final AniListClient aniListClient;
    private final CharacterNameTranslator characterNameTranslator;
    private final AniListSearchQueryGenerator aniListSearchQueryGenerator;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final TransactionOperations transactionOperations;

    public Page<CharacterBirthdayResponse> getBirthdays(Integer month, Integer day, Pageable pageable) {
        Page<Character> page = characterRepository.searchBirthdays(month, day, pageable);
        List<CharacterBirthdayResponse> content = page.getContent().stream()
                .filter(this::hasValidBirthday)
                .map(this::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public List<CharacterBirthdayCalendarResponse> getCalendar(Integer month) {
        return characterRepository.findByBirthdayMonth(month).stream()
                .filter(this::hasValidBirthday)
                .collect(Collectors.groupingBy(Character::getBirthdayDay))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new CharacterBirthdayCalendarResponse(
                        month,
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(Character::getNameKo))
                                .map(this::toResponse)
                                .toList()
                ))
                .toList();
    }

    public List<CharacterBirthdayResponse> getUpcoming(int limit) {
        return characterRepository.findAllWithBirthday().stream()
                .filter(this::hasValidBirthday)
                .map(this::toResponse)
                .sorted(Comparator
                        .comparingInt(CharacterBirthdayResponse::getDaysUntilBirthday)
                        .thenComparing(CharacterBirthdayResponse::getBirthdayMonth)
                        .thenComparing(CharacterBirthdayResponse::getBirthdayDay)
                        .thenComparing(CharacterBirthdayResponse::getNameKo))
                .limit(limit)
                .toList();
    }

    public Page<CharacterBirthdayResponse> getMyBirthdays(Long userId, Integer month, Integer day, Pageable pageable) {
        Page<UserCharacterBirthday> page =
                userCharacterBirthdayRepository.searchBirthdays(userId, month, day, pageable);
        List<CharacterBirthdayResponse> content = page.getContent().stream()
                .map(UserCharacterBirthday::getCharacter)
                .filter(this::hasValidBirthday)
                .map(this::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    public List<CharacterBirthdayCalendarResponse> getMyCalendar(Long userId, Integer month) {
        return userCharacterBirthdayRepository.findByUserIdAndBirthdayMonth(userId, month).stream()
                .map(UserCharacterBirthday::getCharacter)
                .filter(this::hasValidBirthday)
                .collect(Collectors.groupingBy(Character::getBirthdayDay))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new CharacterBirthdayCalendarResponse(
                        month,
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(Character::getNameKo))
                                .map(this::toResponse)
                                .toList()
                ))
                .toList();
    }

    public List<CharacterBirthdayResponse> getMyUpcoming(Long userId, int limit) {
        return userCharacterBirthdayRepository.findAllWithBirthdayByUserId(userId).stream()
                .map(UserCharacterBirthday::getCharacter)
                .filter(this::hasValidBirthday)
                .map(this::toResponse)
                .sorted(Comparator
                        .comparingInt(CharacterBirthdayResponse::getDaysUntilBirthday)
                        .thenComparing(CharacterBirthdayResponse::getBirthdayMonth)
                        .thenComparing(CharacterBirthdayResponse::getBirthdayDay)
                        .thenComparing(CharacterBirthdayResponse::getNameKo))
                .limit(limit)
                .toList();
    }

    public CharacterBirthdaySearchResponse search(String query, String work, boolean includeExternal) {
        List<Character> dbResults = searchRegisteredCharacters(query);
        if (!dbResults.isEmpty() || !includeExternal) {
            return new CharacterBirthdaySearchResponse(
                    dbResults.stream().map(this::toResponse).toList(),
                    List.of()
            );
        }

        return new CharacterBirthdaySearchResponse(
                List.of(),
                searchAniListCandidates(query, work)
        );
    }

    private List<Character> searchRegisteredCharacters(String query) {
        Map<Long, Character> deduplicated = new LinkedHashMap<>();
        for (String keyword : dbSearchKeywords(query)) {
            for (Character character : characterRepository.searchByKeyword(keyword)) {
                Long key = character.getId();
                if (key != null) {
                    deduplicated.putIfAbsent(key, character);
                } else {
                    deduplicated.putIfAbsent((long) System.identityHashCode(character), character);
                }
            }
        }
        return deduplicated.values().stream()
                .filter(this::hasValidBirthday)
                .toList();
    }

    private List<String> dbSearchKeywords(String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery == null) {
            return List.of();
        }

        return List.of(normalizedQuery);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CharacterBirthdayResponse registerFromAniList(CharacterBirthdayRegisterRequest request) {
        String externalId = normalize(request.externalId());
        return toResponse(findOrRegisterAniListCharacter(request, externalId));
    }

    @Transactional
    public CharacterBirthdayResponse addToMyBirthdays(Long userId, Long characterId) {
        return userCharacterBirthdayRepository.findByUserIdAndCharacterId(userId, characterId)
                .map(UserCharacterBirthday::getCharacter)
                .map(this::toResponse)
                .orElseGet(() -> addNewUserCharacterBirthday(userId, characterId));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CharacterBirthdayResponse registerFromAniListToMyBirthdays(Long userId,
                                                                      CharacterBirthdayRegisterRequest request) {
        String externalId = normalize(request.externalId());
        Character character = findOrRegisterAniListCharacter(request, externalId);
        return transactionOperations.execute(status -> addToMyBirthdays(userId, character.getId()));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CharacterBirthdaySearchAddResponse searchAndAddToMyBirthdays(Long userId,
                                                                        CharacterBirthdaySearchAddRequest request) {
        List<Character> dbResults = searchRegisteredCharacters(request.query());
        if (!dbResults.isEmpty()) {
            Character character = dbResults.get(0);
            CharacterBirthdayResponse added =
                    transactionOperations.execute(status -> addToMyBirthdays(userId, character.getId()));
            return CharacterBirthdaySearchAddResponse.added(added);
        }

        return CharacterBirthdaySearchAddResponse.candidates(searchAniListCandidates(request.query(), request.work()));
    }

    @Transactional
    public void removeFromMyBirthdays(Long userId, Long characterId) {
        long deleted = userCharacterBirthdayRepository.deleteByUserIdAndCharacterId(userId, characterId);
        if (deleted == 0) {
            throw new CustomException(ErrorCode.USER_CHARACTER_BIRTHDAY_NOT_FOUND);
        }
    }

    private Character findOrRegisterAniListCharacter(CharacterBirthdayRegisterRequest request, String externalId) {
        return characterRepository.findBySourceTypeAndExternalId(SOURCE_TYPE_ANILIST, externalId)
                .orElseGet(() -> createOrFindExisting(request, externalId));
    }

    private Character createOrFindExisting(CharacterBirthdayRegisterRequest request, String externalId) {
        try {
            return transactionOperations.execute(status ->
                    characterRepository.save(createCharacter(toAniListResult(request), request.nameKo()))
            );
        } catch (DataIntegrityViolationException e) {
            return characterRepository.findBySourceTypeAndExternalId(SOURCE_TYPE_ANILIST, externalId)
                    .orElseThrow(() -> e);
        }
    }

    private CharacterBirthdayResponse addNewUserCharacterBirthday(Long userId, Long characterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Character character = characterRepository.findByIdWithIpTitle(characterId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));
        if (!hasValidBirthday(character)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, "생일이 유효한 캐릭터만 내 목록에 추가할 수 있습니다.");
        }

        try {
            UserCharacterBirthday saved = userCharacterBirthdayRepository.saveAndFlush(
                    UserCharacterBirthday.builder()
                            .user(user)
                            .character(character)
                            .build()
            );
            return toResponse(saved.getCharacter());
        } catch (DataIntegrityViolationException e) {
            return userCharacterBirthdayRepository.findByUserIdAndCharacterId(userId, characterId)
                    .map(UserCharacterBirthday::getCharacter)
                    .map(this::toResponse)
                    .orElseThrow(() -> e);
        }
    }

    private List<AniListCharacterCandidateResponse> searchAniListCandidates(String query, String work) {
        String normalizedWork = normalize(work);
        List<AniListCharacterResult> results = searchAniListWithVariants(query);
        return results.stream()
                .filter(this::hasBirthday)
                .filter(result -> matchesWork(result, normalizedWork))
                .sorted(Comparator
                        .comparingInt((AniListCharacterResult result) -> relevanceScore(query, result))
                        .reversed())
                .map(AniListCharacterCandidateResponse::from)
                .toList();
    }

    private List<AniListCharacterResult> searchAniListWithVariants(String query) {
        Map<String, AniListCharacterResult> deduplicated = new LinkedHashMap<>();
        for (String variant : aniListSearchQueryGenerator.generate(query)) {
            addAniListResults(deduplicated, aniListClient.searchCharacters(variant));
        }

        for (String fallback : aniListSearchQueryGenerator.splitFallbacks(query)) {
            addAniListResults(deduplicated, aniListClient.searchCharacters(fallback));
        }

        return new ArrayList<>(deduplicated.values());
    }

    private void addAniListResults(Map<String, AniListCharacterResult> deduplicated,
                                   List<AniListCharacterResult> results) {
        for (AniListCharacterResult result : results) {
            String key = result.externalId() != null
                    ? result.externalId()
                    : result.nativeName() + "-" + result.birthdayMonth() + "-" + result.birthdayDay();
            deduplicated.putIfAbsent(key, result);
        }
    }

    private boolean matchesWork(AniListCharacterResult result, String work) {
        if (work == null) {
            return true;
        }
        String mediaTitle = String.join(" ",
                nullToBlank(result.mediaNativeTitle()),
                nullToBlank(result.mediaRomajiTitle()),
                nullToBlank(result.mediaUserPreferredTitle())
        ).toLowerCase();
        return mediaTitle.contains(work.toLowerCase());
    }

    private int relevanceScore(String query, AniListCharacterResult result) {
        String searchable = normalizeForSearch(String.join(" ",
                nullToBlank(result.fullName()),
                nullToBlank(result.userPreferredName()),
                nullToBlank(result.nativeName())
        ));

        int score = 0;
        for (String variant : aniListSearchQueryGenerator.generate(query)) {
            String normalizedVariant = normalizeForSearch(variant);
            String reversedVariant = normalizeForSearch(reverseWords(variant));
            if (normalizedVariant.isBlank()) {
                continue;
            }

            if (searchable.equals(normalizedVariant) || searchable.equals(reversedVariant)) {
                score += 1000;
            }
            if (searchable.contains(normalizedVariant)) {
                score += 300;
            }
            if (searchable.contains(reversedVariant)) {
                score += 300;
            }
            for (String token : normalizedVariant.split(" ")) {
                if (token.length() >= 2 && searchable.contains(token)) {
                    score += 20;
                }
            }
        }
        return score;
    }

    private String normalizeForSearch(String value) {
        return value == null
                ? ""
                : value.toLowerCase().replaceAll("[^a-z0-9\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}]+", " ").trim();
    }

    private String reverseWords(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String[] parts = value.trim().split("\\s+");
        List<String> reversed = new ArrayList<>();
        for (int i = parts.length - 1; i >= 0; i--) {
            reversed.add(parts[i]);
        }
        return String.join(" ", reversed);
    }

    private Character createCharacter(AniListCharacterResult result, String selectedNameKo) {
        IpTitle ipTitle = findOrCreateIpTitle(result);

        return Character.builder()
                .ipTitle(ipTitle)
                .nameKo(selectedNameKo)
                .nameJa(result.nativeName())
                .birthdayMonth(result.birthdayMonth())
                .birthdayDay(result.birthdayDay())
                .imageUrl(result.imageUrl())
                .sourceType(SOURCE_TYPE_ANILIST)
                .externalId(result.externalId())
                .sourceUrl(result.sourceUrl())
                .isAutoTranslated(false)
                .extra(toExtraJson(result))
                .build();
    }

    private IpTitle findOrCreateIpTitle(AniListCharacterResult result) {
        String mediaExternalId = normalize(result.mediaExternalId());
        if (mediaExternalId != null) {
            return ipTitleRepository.findBySourceTypeAndExternalId(SOURCE_TYPE_ANILIST, mediaExternalId)
                    .orElseGet(() -> ipTitleRepository.save(createIpTitle(result)));
        }
        return ipTitleRepository.save(createIpTitle(result));
    }

    private IpTitle createIpTitle(AniListCharacterResult result) {
        String mediaExternalId = normalize(result.mediaExternalId());
        TranslatedName titleName = characterNameTranslator.translate(
                result.mediaNativeTitle(),
                result.mediaRomajiTitle(),
                result.mediaUserPreferredTitle()
        );

        return IpTitle.builder()
                .nameKo(titleName.nameKo())
                .nameJa(titleName.nameJa())
                .nameEn(titleName.nameEn())
                .category(DEFAULT_CATEGORY)
                .thumbnailUrl(result.imageUrl())
                .sourceType(SOURCE_TYPE_ANILIST)
                .externalId(mediaExternalId)
                .isAutoTranslated(titleName.autoTranslated())
                .extra(toExtraJson(result))
                .build();
    }

    private CharacterBirthdayResponse toResponse(Character character) {
        return CharacterBirthdayResponse.from(
                character,
                daysUntilBirthday(character.getBirthdayMonth(), character.getBirthdayDay())
        );
    }

    private int daysUntilBirthday(Integer month, Integer day) {
        LocalDate today = LocalDate.now(clock);
        LocalDate birthday = nextBirthday(today, month, day);
        return (int) java.time.temporal.ChronoUnit.DAYS.between(today, birthday);
    }

    private boolean hasValidBirthday(Character character) {
        return isValidBirthday(character.getBirthdayMonth(), character.getBirthdayDay());
    }

    private boolean isValidBirthday(Integer month, Integer day) {
        if (month == null || day == null) {
            return false;
        }
        try {
            MonthDay.of(month, day);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    private LocalDate nextBirthday(LocalDate today, Integer month, Integer day) {
        int year = today.getYear();
        LocalDate birthday = birthdayInYear(year, month, day);
        if (birthday.isBefore(today)) {
            birthday = birthdayInYear(year + 1, month, day);
        }
        return birthday;
    }

    private LocalDate birthdayInYear(int year, Integer month, Integer day) {
        if (month == 2 && day == 29) {
            int leapYear = year;
            while (!java.time.Year.isLeap(leapYear)) {
                leapYear++;
            }
            return LocalDate.of(leapYear, month, day);
        }
        return LocalDate.of(year, month, day);
    }

    private boolean hasBirthday(AniListCharacterResult result) {
        return result.birthdayMonth() != null && result.birthdayDay() != null;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private String toExtraJson(AniListCharacterResult result) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "provider", SOURCE_TYPE_ANILIST,
                    "raw", result.rawJson() == null ? "" : result.rawJson()
            ));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private AniListCharacterResult toAniListResult(CharacterBirthdayRegisterRequest request) {
        return new AniListCharacterResult(
                normalize(request.externalId()),
                request.nativeName(),
                request.fullName(),
                request.userPreferredName(),
                request.birthdayMonth(),
                request.birthdayDay(),
                request.imageUrl(),
                normalize(request.mediaExternalId()),
                request.mediaNativeTitle(),
                request.mediaRomajiTitle(),
                request.mediaUserPreferredTitle(),
                request.sourceUrl(),
                request.rawJson()
        );
    }
}
