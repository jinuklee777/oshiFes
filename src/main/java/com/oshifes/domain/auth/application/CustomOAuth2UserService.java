package com.oshifes.domain.auth.application;

import com.oshifes.domain.user.dao.UserRepository;
import com.oshifes.domain.user.entity.User;
import com.oshifes.domain.user.entity.UserRole;
import com.oshifes.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;
        try {
            oAuth2User = super.loadUser(request);
        } catch (IllegalArgumentException e) {
            throw missingUserIdentifierException();
        }

        String provider = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get("sub");
        if (providerId == null) {
            throw missingUserIdentifierException();
        }
        String nickname = (String) attributes.getOrDefault("name", "Unknown");
        String profileImageUrl = (String) attributes.getOrDefault("picture", "");

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existing -> {
                    existing.updateProfile(nickname, profileImageUrl);
                    return existing;
                })
                .orElseGet(() -> userRepository.save(
                        User.createNewUser(provider, providerId, nickname, profileImageUrl, UserRole.USER)
                ));

        return UserPrincipal.of(user, attributes);
    }

    private OAuth2AuthenticationException missingUserIdentifierException() {
        return new OAuth2AuthenticationException(new OAuth2Error(
                "missing_user_identifier",
                "OAuth2 사용자 식별자(sub)를 찾을 수 없습니다.",
                null
        ));
    }
}
