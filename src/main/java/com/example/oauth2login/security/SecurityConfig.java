package com.example.oauth2login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔒 Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/error", "/webjars/**",
                                "/h2-console/**" // ✅ Allow H2 Console access
                        ).permitAll()
                        .requestMatchers("/profile/**").authenticated()
                        .anyRequest().permitAll()
                )

                // 🔑 OAuth2 login configuration
                .oauth2Login(oauth -> oauth
                        .loginPage("/")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/profile", true)
                )

                // 🚪 Logout configuration (GET-friendly)
                .logout(logout -> logout
                        .logoutUrl("/logout")              // endpoint for logout
                        .logoutSuccessUrl("/")             // redirect home
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // 🛡️ CSRF: disable for H2 console (and generally for dev)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable() // ✅ fully disable CSRF to prevent blocking H2 console forms
                )

                // 🪟 Allow H2 console to use frames (iframe requirement)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // ✅ this replaces StaticHeadersWriter
                );

        return http.build();
    }
}
