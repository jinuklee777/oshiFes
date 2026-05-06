package com.oshifes.domain.ip.api;

import com.oshifes.domain.ip.application.CharacterBirthdayService;
import com.oshifes.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CharacterBirthdayControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CharacterBirthdayService characterBirthdayService;

    @Test
    void characterBirthdayApis_arePublicWhitelisted() {
        String[] whitelist = (String[]) ReflectionTestUtils.getField(SecurityConfig.class, "PUBLIC_API_WHITELIST");

        assertThat(whitelist)
                .contains("/api/characters/birthdays", "/api/characters/birthdays/**");
    }

    @Test
    void getBirthdays_doesNotRequireAuthentication() throws Exception {
        given(characterBirthdayService.getBirthdays(isNull(), isNull(), any()))
                .willReturn(Page.empty());

        mockMvc.perform(get("/api/characters/birthdays"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
