// src/main/java/org/andi/librarymanagementbackend/service/impl/UserDetailsServiceImpl.java
package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.andi.librarymanagementbackend.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring Security's UserDetailsService
 * to load user data by email.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repo;

    /**
     * Constructor.
     *
     * @param repo the UserRepository
     */
    public UserDetailsServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    /**
     * Load user by username (email).
     *
     * @param email the user's email
     * @return the UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);
    }
}
