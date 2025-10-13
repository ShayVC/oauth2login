package com.example.oauth2login.security;

import com.example.oauth2login.model.User;
import com.example.oauth2login.service.AuthProviderService;
import com.example.oauth2login.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AuthProviderService authProviderService;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   AuthProviderService authProviderService) {
        this.userRepository = userRepository;
        this.authProviderService = authProviderService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String oauthId = null;
        String displayName = null;
        String avatarUrl = null;

        // ---- GOOGLE LOGIN ----
        if (provider.equals("GOOGLE")) {
            email = (String) attributes.get("email");
            oauthId = (String) attributes.get("sub");
            displayName = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("picture");
        }

        // ---- GITHUB LOGIN ----
        else if (provider.equals("GITHUB")) {
            email = (String) attributes.get("email");
            if (email == null) { // GitHub sometimes hides email
                email = "no-email@" + attributes.get("login") + ".github";
            }
            oauthId = String.valueOf(attributes.get("id"));
            displayName = (String) attributes.get("name");
            if (displayName == null || displayName.isEmpty()) {
                displayName = (String) attributes.get("login");
            }
            avatarUrl = (String) attributes.get("avatar_url");
        }

        // ---- MAKE VARIABLES FINAL FOR LAMBDA ----
        final String finalEmail = email;
        final String finalOauthId = oauthId;
        final String finalDisplayName = displayName;
        final String finalAvatarUrl = avatarUrl;

        // ---- FIND OR CREATE USER ----
        User user = userRepository.findByEmail(finalEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setOauthId(finalOauthId);
            newUser.setEmail(finalEmail);
            newUser.setDisplayName(finalDisplayName);
            newUser.setAvatarUrl(finalAvatarUrl);
            newUser.setProvider(provider);
            return userRepository.save(newUser);
        });

        // ---- UPDATE EXISTING USER INFO ----
        user.setDisplayName(finalDisplayName);
        user.setAvatarUrl(finalAvatarUrl);
        user.setProvider(provider);
        userRepository.save(user);

        // ---- LINK PROVIDER ----
        authProviderService.linkProvider(user, provider, finalOauthId, finalEmail);

        return oAuth2User;
    }
}
