package com.oshifes.domain.auth.application;

import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.domain.user.entity.UserRole;
import com.oshifes.global.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    private static final String USER_INFO_URI = "https://example.com/userinfo";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    void loadUser_newUser_savesUser() {
        MockRestServiceServer server = configureOAuth2UserInfoResponse();
        given(userRepository.findByProviderAndProviderId("google", "provider-id")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        OAuth2User oauth2User = customOAuth2UserService.loadUser(oAuth2UserRequest());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertThat(savedUser.getProvider()).isEqualTo("google");
        assertThat(savedUser.getProviderId()).isEqualTo("provider-id");
        assertThat(savedUser.getNickname()).isEqualTo("Tester");
        assertThat(savedUser.getProfileImageUrl()).isEqualTo("https://example.com/profile.png");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(((UserPrincipal) oauth2User).getRole()).isEqualTo("USER");
        server.verify();
    }

    @Test
    void loadUser_existingUser_updatesProfile() {
        MockRestServiceServer server = configureOAuth2UserInfoResponse();
        User existingUser = User.createNewUser("google", "provider-id", "Old", "old.png", UserRole.USER);
        given(userRepository.findByProviderAndProviderId("google", "provider-id"))
                .willReturn(Optional.of(existingUser));

        customOAuth2UserService.loadUser(oAuth2UserRequest());

        assertThat(existingUser.getNickname()).isEqualTo("Tester");
        assertThat(existingUser.getProfileImageUrl()).isEqualTo("https://example.com/profile.png");
        verify(userRepository, never()).save(any(User.class));
        server.verify();
    }

    private MockRestServiceServer configureOAuth2UserInfoResponse() {
        RestTemplate restTemplate = new RestTemplate();
        customOAuth2UserService.setRestOperations(restTemplate);
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo(USER_INFO_URI))
                .andRespond(withSuccess("""
                        {
                          "sub": "provider-id",
                          "name": "Tester",
                          "picture": "https://example.com/profile.png"
                        }
                        """, MediaType.APPLICATION_JSON));
        return server;
    }

    private OAuth2UserRequest oAuth2UserRequest() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/login/oauth2/code/google")
                .authorizationUri("https://example.com/oauth2/authorize")
                .tokenUri("https://example.com/oauth2/token")
                .userInfoUri(USER_INFO_URI)
                .userNameAttributeName("sub")
                .clientName("Google")
                .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access-token",
                Instant.now(),
                Instant.now().plusSeconds(60)
        );
        return new OAuth2UserRequest(clientRegistration, accessToken);
    }
}
