package com.oshifes.domain.auth.handler;

import com.oshifes.global.security.JwtTokenProvider;
import com.oshifes.global.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2SuccessHandlerTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktbm90LWZvci1wcm9kdWN0aW9u";

    @Test
    void onAuthenticationSuccess_redirectsWithAccessTokenWithoutCookie() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(new JwtTokenProvider(SECRET, 60_000));
        ReflectionTestUtils.setField(handler, "redirectUri", "oshifes://oauth2/callback");
        UserPrincipal principal = UserPrincipal.of(1L, "USER", Map.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

        assertThat(response.getHeader("Set-Cookie")).isNull();
        assertThat(response.getRedirectedUrl()).startsWith("oshifes://oauth2/callback?");
        assertThat(response.getRedirectedUrl()).contains("tokenType=Bearer");

        String accessToken = UriComponentsBuilder.fromUriString(response.getRedirectedUrl())
                .build()
                .getQueryParams()
                .getFirst("accessToken");
        assertThat(accessToken).isNotBlank();
    }
}
