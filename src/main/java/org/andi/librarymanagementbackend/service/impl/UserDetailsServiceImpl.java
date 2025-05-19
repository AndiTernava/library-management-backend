package org.andi.librarymanagementbackend.service.impl;

import org.andi.librarymanagementbackend.model.User;
import org.andi.librarymanagementbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.andi.librarymanagementbackend.security.CustomUserDetails;  // new import
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repo;

    public UserDetailsServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);  // use the new class
    }
}
