package com.example.oauth2login.model;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_providers")
public class AuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each provider record links to a single user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String provider; // GOOGLE or GITHUB

    @Column(nullable = false, unique = true)
    private String providerUserId; // e.g. "sub" (Google) or "id" (GitHub)

    @Column(nullable = false)
    private String providerEmail; // OAuth provider’s email (may differ from main email)

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getProviderUserId() { return providerUserId; }
    public void setProviderUserId(String providerUserId) { this.providerUserId = providerUserId; }

    public String getProviderEmail() { return providerEmail; }
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }
}
