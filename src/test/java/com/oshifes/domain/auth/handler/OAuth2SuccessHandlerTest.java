package com.oshifes.domain.auth.handler;

import com.oshifes.global.security.JwtTokenProvider;
import com.oshifes.global.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2SuccessHandlerTest {

    private static final String SECRET =
            "dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLW9ubHktbm90LWZvci1wcm9kdWN0aW9u";

    @Test
    void onAuthenticationSuccess_setsAccessTokenCookieAndRedirectsWithoutTokenInUrl() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(new JwtTokenProvider(SECRET, 60_000));
        ReflectionTestUtils.setField(handler, "redirectUri", "http://localhost:3000/oauth2/callback");
        ReflectionTestUtils.setField(handler, "expirationMs", 60_000L);
        ReflectionTestUtils.setField(handler, "cookieSecure", true);
        ReflectionTestUtils.setField(handler, "cookieSameSite", "Lax");
        UserPrincipal principal = UserPrincipal.of(1L, "USER", Map.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

        String cookie = response.getHeader(HttpHeaders.SET_COOKIE);
        assertThat(cookie).isNotBlank();
        assertThat(cookie).startsWith("access_token=");
        assertThat(cookie).contains("HttpOnly");
        assertThat(cookie).contains("Secure");
        assertThat(cookie).contains("Path=/");
        assertThat(cookie).contains("Max-Age=60");
        assertThat(cookie).contains("SameSite=Lax");
        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:3000/oauth2/callback");
        assertThat(response.getRedirectedUrl()).doesNotContain("token=");
    }
}
