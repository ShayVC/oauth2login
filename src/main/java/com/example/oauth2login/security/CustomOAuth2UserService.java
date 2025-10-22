package com.example.oauth2login.security;

import com.example.oauth2login.model.User;
import com.example.oauth2login.service.AuthProviderService;
import com.example.oauth2login.repository.UserRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
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
            oauthId = String.valueOf(attributes.get("id"));
            displayName = (String) attributes.get("name");
            if (displayName == null || displayName.isEmpty()) {
                displayName = (String) attributes.get("login");
            }
            avatarUrl = (String) attributes.get("avatar_url");
            email = (String) attributes.get("email");

            // GitHub sometimes hides email; fetch via /user/emails
            if (email == null) {
                email = fetchGithubPrimaryEmail(userRequest.getAccessToken().getTokenValue());
            }

            // Still null? use a fallback (should rarely happen)
            if (email == null) {
                email = "no-email@" + attributes.get("login") + ".github";
            }
        }

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider: " + provider);
        }

        // ---- FIND OR CREATE USER ----
        Optional<User> existingUserOpt = userRepository.findByEmail(email);
        User user;

        if (existingUserOpt.isPresent()) {
            // existing user (maybe logged in with another provider)
            user = existingUserOpt.get();
            user.setOauthId(oauthId);
            user.setDisplayName(displayName);
            user.setAvatarUrl(avatarUrl);
            user.setProvider(provider);
        } else {
            // new user
            user = new User();
            user.setOauthId(oauthId);
            user.setEmail(email);
            user.setDisplayName(displayName);
            user.setAvatarUrl(avatarUrl);
            user.setProvider(provider);
        }

        userRepository.save(user);

        // ---- LINK PROVIDER RECORD ----
        authProviderService.linkProvider(user, provider, oauthId, email);

        return oAuth2User;
    }

    /**
     * Fetch the user's verified primary email from GitHub's /user/emails endpoint.
     */
    private String fetchGithubPrimaryEmail(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "token " + accessToken);
            headers.add("Accept", "application/vnd.github+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                for (Map<String, Object> e : emails) {
                    Boolean primary = (Boolean) e.get("primary");
                    Boolean verified = (Boolean) e.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) e.get("email");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch GitHub email: " + e.getMessage());
        }
        return null;
    }
}
