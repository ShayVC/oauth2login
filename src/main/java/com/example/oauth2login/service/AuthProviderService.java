package com.example.oauth2login.service;

import com.example.oauth2login.model.AuthProvider;
import com.example.oauth2login.model.User;
import com.example.oauth2login.repository.AuthProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthProviderService {

    private final AuthProviderRepository authProviderRepository;

    public AuthProviderService(AuthProviderRepository authProviderRepository) {
        this.authProviderRepository = authProviderRepository;
    }

    @Transactional
    public AuthProvider linkProvider(User user, String provider, String providerUserId, String providerEmail) {
        Optional<AuthProvider> existing = authProviderRepository.findByProviderAndProviderUserId(provider, providerUserId);

        if (existing.isPresent()) {
            return existing.get();
        }

        AuthProvider newAuthProvider = new AuthProvider();
        newAuthProvider.setUser(user);
        newAuthProvider.setProvider(provider);
        newAuthProvider.setProviderUserId(providerUserId);
        newAuthProvider.setProviderEmail(providerEmail);

        return authProviderRepository.save(newAuthProvider);
    }
}
