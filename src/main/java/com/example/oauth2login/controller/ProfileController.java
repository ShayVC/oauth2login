package com.example.oauth2login.controller;

import com.example.oauth2login.model.User;
import com.example.oauth2login.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ----- View Profile -----
    @GetMapping("/profile")
    public String viewProfile(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            // user is not authenticated, redirect home
            return "redirect:/";
        }

        // Try to get email first (Google & GitHub)
        String email = (String) principal.getAttributes().get("email");
        // Fallback for GitHub: use login as unique id
        String oauthId = String.valueOf(principal.getAttributes().get("sub") != null
                ? principal.getAttributes().get("sub")
                : principal.getAttributes().get("id"));

        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        if (user == null && oauthId != null) {
            user = userRepository.findByOauthId(oauthId).orElse(null);
        }

        if (user == null) {
            model.addAttribute("errorMessage", "User record not found.");
            return "error";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // ----- Update Profile -----
    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal OAuth2User principal,
                                @RequestParam String displayName,
                                @RequestParam String bio) {
        if (principal == null) {
            return "redirect:/";
        }

        String email = (String) principal.getAttributes().get("email");
        String oauthId = String.valueOf(principal.getAttributes().get("sub") != null
                ? principal.getAttributes().get("sub")
                : principal.getAttributes().get("id"));

        User user = null;
        if (email != null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        if (user == null && oauthId != null) {
            user = userRepository.findByOauthId(oauthId).orElse(null);
        }

        if (user != null) {
            user.setDisplayName(displayName);
            user.setBio(bio);
            userRepository.save(user);
        }

        return "redirect:/profile";
    }
}