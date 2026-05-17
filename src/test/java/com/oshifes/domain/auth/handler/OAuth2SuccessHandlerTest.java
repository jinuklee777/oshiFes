package com.oshifes.domain.auth.handler;

import com.oshifes.domain.auth.application.OAuth2AuthorizationCodeService;
import com.oshifes.global.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OAuth2SuccessHandlerTest {

    @Test
    void onAuthenticationSuccess_redirectsWithAuthorizationCodeWithoutAccessToken() throws Exception {
        OAuth2AuthorizationCodeService authorizationCodeService = mock(OAuth2AuthorizationCodeService.class);
        given(authorizationCodeService.issueCode(1L, "USER")).willReturn("login-code");
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(authorizationCodeService);
        ReflectionTestUtils.setField(handler, "redirectUri", "oshifes://oauth2/callback");
        UserPrincipal principal = UserPrincipal.of(1L, "USER", Map.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

        assertThat(response.getRedirectedUrl()).doesNotContain("accessToken");
        assertThat(response.getHeader("Set-Cookie")).isNull();

        String code = UriComponentsBuilder.fromUriString(response.getRedirectedUrl())
                .build()
                .getQueryParams()
                .getFirst("code");
        assertThat(code).isEqualTo("login-code");
    }
}
