package com.moneymanager.service;

import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        ProfileEntity existedProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profile not found with email :" + email));
        return User.builder()
                .username(existedProfile.getEmail())
                .password(existedProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
