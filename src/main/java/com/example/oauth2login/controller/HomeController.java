package com.example.oauth2login.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("name", getDisplayName(principal));
            model.addAttribute("email", principal.getAttribute("email"));
            model.addAttribute("imageUrl", getAvatarUrl(principal));
        }
        return "home";
    }

    private String getDisplayName(OAuth2User principal) {
        // Google uses "name", GitHub uses "login"
        String name = principal.getAttribute("name");
        if (name == null) {
            name = principal.getAttribute("login");
        }
        return name != null ? name : "User";
    }

    private String getAvatarUrl(OAuth2User principal) {
        // Google uses "picture", GitHub uses "avatar_url"
        String imageUrl = principal.getAttribute("picture");
        if (imageUrl == null) {
            imageUrl = principal.getAttribute("avatar_url");
        }
        return imageUrl;
    }
}
