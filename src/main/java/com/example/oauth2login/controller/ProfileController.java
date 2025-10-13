package com.example.oauth2login.controller;

import com.example.oauth2login.model.User;
import com.example.oauth2login.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🟢 Show profile page after login
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal OAuth2User oAuth2User,
                              OAuth2AuthenticationToken authentication,
                              Model model) {

        if (oAuth2User == null) {
            return "redirect:/";
        }

        Optional<User> optionalUser = findUser(authentication);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            model.addAttribute("user", user);
            // ✅ CHANGE 1: Explicitly set 'error' to null or empty string on success
            model.addAttribute("error", null);
        } else {
            // User not found case
            // ✅ CHANGE 2: Explicitly set 'user' to null on failure
            model.addAttribute("user", null);
            model.addAttribute("error", "User not found.");
        }

        return "profile";
    }

    // 🟡 Update user profile (displayName, bio)
    // ... (This method remains unchanged, but you might want to add
    //      model.addAttribute("error", null); for consistency if successful)
    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal OAuth2User oAuth2User,
                                OAuth2AuthenticationToken authentication,
                                @RequestParam String displayName,
                                @RequestParam String bio,
                                Model model) {

        if (oAuth2User == null) {
            return "redirect:/";
        }

        Optional<User> optionalUser = findUser(authentication);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDisplayName(displayName);
            user.setBio(bio);
            userRepository.save(user);

            model.addAttribute("user", user);
            model.addAttribute("success", "Profile updated successfully!");
            model.addAttribute("error", null); // Set error to null on success
        } else {
            model.addAttribute("user", null); // Set user to null on failure
            model.addAttribute("error", "User not found. Could not update.");
        }

        return "profile";
    }

    // 🔹 Helper method to find user by email or OAuth ID (works for both Google & GitHub)
    private Optional<User> findUser(OAuth2AuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        String registrationId = authentication.getAuthorizedClientRegistrationId();

        String email = (String) attributes.get("email");
        String oauthId = null;

        if ("github".equalsIgnoreCase(registrationId)) {
            oauthId = String.valueOf(attributes.get("id"));
        } else if ("google".equalsIgnoreCase(registrationId)) {
            oauthId = (String) attributes.get("sub");
        }

        return (email != null)
                ? userRepository.findByEmail(email)
                : userRepository.findByOauthId(oauthId);
    }
}