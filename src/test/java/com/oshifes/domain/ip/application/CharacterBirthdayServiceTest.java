package com.oshifes.domain.ip.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oshifes.domain.ip.api.dto.AniListCharacterCandidateResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayRegisterRequest;
import com.oshifes.domain.ip.api.dto.CharacterBirthdayResponse;
import com.oshifes.domain.ip.api.dto.CharacterBirthdaySearchResponse;
import com.oshifes.domain.ip.dao.CharacterRepository;
import com.oshifes.domain.ip.dao.IpTitleRepository;
import com.oshifes.domain.ip.entity.Character;
import com.oshifes.domain.ip.entity.IpTitle;
import com.oshifes.infrastructure.anilist.AniListCharacterResult;
import com.oshifes.infrastructure.anilist.AniListClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CharacterBirthdayServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private IpTitleRepository ipTitleRepository;

    @Mock
    private AniListClient aniListClient;

    private CharacterBirthdayService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-05-06T00:00:00Z"), ZoneId.of("Asia/Seoul"));
        service = new CharacterBirthdayService(
                characterRepository,
                ipTitleRepository,
                aniListClient,
                new FallbackCharacterNameTranslator(),
                new AniListSearchQueryGenerator(),
                new ObjectMapper(),
                clock
        );
    }

    @Test
    void search_dbResultExists_doesNotCallAniList() {
        Character character = character("나카노 아즈사", 11, 11);
        given(characterRepository.searchByKeyword("아즈사")).willReturn(List.of(character));

        CharacterBirthdaySearchResponse result = service.search("아즈사", null, true);

        assertThat(result.registered()).hasSize(1);
        assertThat(result.candidates()).isEmpty();
        assertThat(result.registered().get(0).getNameKo()).isEqualTo("나카노 아즈사");
        verify(aniListClient, never()).searchCharacters(any());
    }

    @Test
    void search_fullNameDbMiss_doesNotMatchRegisteredCharactersByNameParts() {
        given(characterRepository.searchByKeyword("아즈사 우이")).willReturn(List.of());
        given(aniListClient.searchCharacters(any())).willReturn(List.of());

        CharacterBirthdaySearchResponse result = service.search("아즈사 우이", null, true);

        assertThat(result.registered()).isEmpty();
        assertThat(result.candidates()).isEmpty();
        verify(characterRepository, never()).searchByKeyword("아즈사");
        verify(characterRepository, never()).searchByKeyword("우이");
    }

    @Test
    void search_namePartCanMatchMultipleRegisteredCharacters() {
        Character azusa = character("나카노 아즈사", 11, 11);
        Character azusaOtherWork = character("시라토리 아즈사", 4, 8);
        given(characterRepository.searchByKeyword("아즈사")).willReturn(List.of(azusa, azusaOtherWork));

        CharacterBirthdaySearchResponse result = service.search("아즈사", null, true);

        assertThat(result.registered())
                .extracting(CharacterBirthdayResponse::getNameKo)
                .containsExactly("나카노 아즈사", "시라토리 아즈사");
        assertThat(result.candidates()).isEmpty();
        verify(aniListClient, never()).searchCharacters(any());
    }

    @Test
    void search_dbMiss_fetchesAniListCandidatesWithoutSaving() {
        AniListCharacterResult aniListResult = aniListResult("1", "中野 梓", "Azusa Nakano", 11, 11);
        given(characterRepository.searchByKeyword("아즈사")).willReturn(List.of());
        given(aniListClient.searchCharacters(any())).willReturn(List.of(aniListResult));

        CharacterBirthdaySearchResponse result = service.search("아즈사", null, true);

        assertThat(result.registered()).isEmpty();
        assertThat(result.candidates()).hasSize(1);
        assertThat(result.candidates().get(0).getNativeName()).isEqualTo("中野 梓");
        assertThat(result.candidates().get(0).getBirthdayMonth()).isEqualTo(11);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void search_aniListResultWithoutBirthday_doesNotSave() {
        AniListCharacterResult aniListResult = aniListResult("1", "中野 梓", "Azusa Nakano", null, null);
        given(characterRepository.searchByKeyword("아즈사")).willReturn(List.of());
        given(aniListClient.searchCharacters(any())).willReturn(List.of(aniListResult));

        CharacterBirthdaySearchResponse result = service.search("아즈사", null, true);

        assertThat(result.registered()).isEmpty();
        assertThat(result.candidates()).isEmpty();
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void search_koreanQuery_usesRomajiAndReversedVariantsForAniList() {
        AniListCharacterResult aniListResult = aniListResult("1", "中野 梓", "Azusa Nakano", 11, 11);
        given(characterRepository.searchByKeyword("나카노 아즈사")).willReturn(List.of());
        given(aniListClient.searchCharacters(any())).willReturn(List.of());
        given(aniListClient.searchCharacters("azusa nakano")).willReturn(List.of(aniListResult));

        CharacterBirthdaySearchResponse result = service.search("나카노 아즈사", null, true);

        assertThat(result.candidates()).hasSize(1);
        verify(aniListClient).searchCharacters("나카노 아즈사");
        verify(aniListClient).searchCharacters("azusa nakano");
        verify(aniListClient).searchCharacters("nakano azusa");
        verify(aniListClient).searchCharacters("아즈사 나카노");
        verify(aniListClient).searchCharacters("nakano ajeusa");
        verify(aniListClient).searchCharacters("ajeusa nakano");
    }

    @Test
    void search_koreanQuery_sortsExactNameCandidateBeforeFamilyNameMatches() {
        AniListCharacterResult itsuki = aniListResult("2", "中野五月", "Itsuki Nakano", 5, 6);
        AniListCharacterResult azusa = aniListResult("1", "中野 梓", "Azusa Nakano", 11, 11);
        given(characterRepository.searchByKeyword("나카노 아즈사")).willReturn(List.of());
        given(aniListClient.searchCharacters(any())).willReturn(List.of(itsuki, azusa));

        CharacterBirthdaySearchResponse result = service.search("나카노 아즈사", null, true);

        assertThat(result.candidates())
                .extracting(candidate -> candidate.getFullName())
                .startsWith("Azusa Nakano");
    }

    @Test
    void registerFromAniList_savesSelectedCandidateWithUserKoreanName() {
        CharacterBirthdayRegisterRequest request = registerRequest("1", "나카노 아즈사");
        given(characterRepository.findBySourceTypeAndExternalId("ANILIST", "1")).willReturn(Optional.empty());
        given(ipTitleRepository.findBySourceTypeAndExternalId("ANILIST", "100")).willReturn(Optional.empty());
        given(ipTitleRepository.save(any(IpTitle.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(characterRepository.save(any(Character.class))).willAnswer(invocation -> invocation.getArgument(0));

        CharacterBirthdayResponse result = service.registerFromAniList(request);

        assertThat(result.getNameKo()).isEqualTo("나카노 아즈사");
        assertThat(result.getNameJa()).isEqualTo("中野 梓");
        assertThat(result.isAutoTranslated()).isFalse();
        verify(characterRepository).save(any(Character.class));
    }

    @Test
    void registerFromAniList_existingExternalId_returnsExistingWithoutDuplicateSave() {
        Character existing = character("나카노 아즈사", 11, 11);
        given(characterRepository.findBySourceTypeAndExternalId("ANILIST", "1")).willReturn(Optional.of(existing));

        CharacterBirthdayResponse result = service.registerFromAniList(registerRequest("1", "아즈사"));

        assertThat(result.getNameKo()).isEqualTo("나카노 아즈사");
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void getUpcoming_calculatesDaysUntilBirthday() {
        Character today = character("오늘 생일", 5, 6);
        Character thisYear = character("올해 생일", 5, 10);
        Character nextYear = character("지난 생일", 5, 1);
        given(characterRepository.findAllWithBirthday()).willReturn(List.of(nextYear, thisYear, today));

        List<CharacterBirthdayResponse> result = service.getUpcoming(3);

        assertThat(result)
                .extracting(CharacterBirthdayResponse::getNameKo)
                .containsExactly("오늘 생일", "올해 생일", "지난 생일");
        assertThat(result)
                .extracting(CharacterBirthdayResponse::getDaysUntilBirthday)
                .containsExactly(0, 4, 360);
    }

    @Test
    void getUpcoming_february29BirthdayUsesNextLeapYear() {
        Character leapDay = character("윤년 생일", 2, 29);
        given(characterRepository.findAllWithBirthday()).willReturn(List.of(leapDay));

        List<CharacterBirthdayResponse> result = service.getUpcoming(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBirthdayMonth()).isEqualTo(2);
        assertThat(result.get(0).getBirthdayDay()).isEqualTo(29);
        assertThat(result.get(0).getDaysUntilBirthday()).isEqualTo(664);
    }

    @Test
    void getBirthdays_february29BirthdayDoesNotThrowInNonLeapYear() {
        Character leapDay = character("윤년 생일", 2, 29);
        given(characterRepository.searchBirthdays(2, 29, PageRequest.of(0, 20)))
                .willReturn(new org.springframework.data.domain.PageImpl<>(List.of(leapDay)));

        Page<CharacterBirthdayResponse> result = service.getBirthdays(2, 29, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDaysUntilBirthday()).isEqualTo(664);
    }

    @Test
    void getBirthdays_returnsPagedResponses() {
        Character character = character("나카노 아즈사", 11, 11);
        given(characterRepository.searchBirthdays(11, null, PageRequest.of(0, 20)))
                .willReturn(new org.springframework.data.domain.PageImpl<>(List.of(character)));

        Page<CharacterBirthdayResponse> result = service.getBirthdays(11, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDaysUntilBirthday()).isEqualTo(189);
    }

    private Character character(String nameKo, int month, int day) {
        IpTitle ipTitle = IpTitle.builder()
                .nameKo("케이온!")
                .nameJa("けいおん!")
                .nameEn("K-On!")
                .category("anime")
                .build();
        return Character.builder()
                .ipTitle(ipTitle)
                .nameKo(nameKo)
                .nameJa(nameKo)
                .birthdayMonth(month)
                .birthdayDay(day)
                .sourceType("ANILIST")
                .externalId("1")
                .build();
    }

    private AniListCharacterResult aniListResult(String id, String nativeName, String fullName,
                                                Integer month, Integer day) {
        return new AniListCharacterResult(
                id,
                nativeName,
                fullName,
                fullName,
                month,
                day,
                "https://example.com/image.jpg",
                "100",
                "けいおん!",
                "K-On!",
                "K-On!",
                "https://anilist.co/character/" + id,
                "{}"
        );
    }

    private CharacterBirthdayRegisterRequest registerRequest(String id, String nameKo) {
        AniListCharacterCandidateResponse candidate = AniListCharacterCandidateResponse.from(
                aniListResult(id, "中野 梓", "Azusa Nakano", 11, 11)
        );
        return new CharacterBirthdayRegisterRequest(
                nameKo,
                candidate.getExternalId(),
                candidate.getNativeName(),
                candidate.getFullName(),
                candidate.getUserPreferredName(),
                candidate.getBirthdayMonth(),
                candidate.getBirthdayDay(),
                candidate.getImageUrl(),
                candidate.getMediaExternalId(),
                candidate.getMediaNativeTitle(),
                candidate.getMediaRomajiTitle(),
                candidate.getMediaUserPreferredTitle(),
                candidate.getSourceUrl(),
                candidate.getRawJson()
        );
    }
}
