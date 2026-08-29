
package com.moneymanager.service;

import com.moneymanager.dto.AuthDto;
import com.moneymanager.dto.ProfileDto;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.ProfileRepository;
import com.moneymanager.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public ProfileService(
            ProfileRepository profileRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.profileRepository = profileRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    // ============================================================
    // REGISTER PROFILE
    // ============================================================

    public ProfileDto registerProfile(ProfileDto profileDto) {

        // 1. Convert DTO to Entity
        ProfileEntity newProfile = toEntity(profileDto);

        // new accounts should generally be inactive
        newProfile.setIsActive(true);

        // 3. Save profile to database
        newProfile = profileRepository.save(newProfile);

        // sending the welcome email
        emailService.sendWelcomeEmail(newProfile.getFullName(), newProfile.getEmail());

        // 6. Convert Entity back to DTO
        return toDTO(newProfile);
    }


    // deleting the profile with mail id
    @Transactional
    public void deleteProfileByEMail(String email){
        Optional<ProfileEntity> profile = profileRepository.findByEmail(email);
        profile.ifPresent(profileRepository::delete);
        ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found with email : " + email);
    }
    // ============================================================
    // DTO -> ENTITY
    // ============================================================

    public ProfileEntity toEntity(ProfileDto profileDto) {

        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())

                // Never store raw password in database
                .password(passwordEncoder.encode(profileDto.getPassword()))

                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }


    // ============================================================
    // ENTITY -> DTO
    // ============================================================

    public ProfileDto toDTO(ProfileEntity profileEntity) {

        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())

                // NOTE:
                // Password should ideally NOT be returned in API responses.
                // We intentionally do not expose it here.
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }


    // ============================================================
    // ACTIVATE PROFILE
    // ============================================================

    public boolean activateProfile(String activationToken) {

        return profileRepository.findByActivationToken(activationToken)
                .map(profileEntity -> {

                    // Activate account
                    profileEntity.setIsActive(true);

                    // Token should not remain reusable
                    profileEntity.setActivationToken(null);

                    // Save changes
                    profileRepository.save(profileEntity);

                    return true;
                })
                .orElse(false);
    }


    // ============================================================
    // CHECK ACCOUNT ACTIVE
    // ============================================================

    public boolean isAccountActive(String email) {

        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }


    // ============================================================
    // GET CURRENT LOGGED-IN PROFILE
    // ============================================================

    public ProfileEntity getCurrentProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null) {

            throw new UsernameNotFoundException(
                    "No authenticated user found"
            );
        }

        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Profile not found with email : "
                                        + authentication.getName()
                        )
                );
    }


    // ============================================================
    // GET PUBLIC PROFILE
    // ============================================================

    public ProfileDto getPublicProfile(String email) {

        ProfileEntity currentUser;

        if (email == null || email.isBlank()) {

            currentUser = getCurrentProfile();

        } else {

            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Profile not found with email : " + email
                            )
                    );
        }

        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }



    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {

        try {

            // 1. Authenticate email + password
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authDto.getEmail(),
                                    authDto.getPassword()
                            )
                    );

            // 2. Get authenticated UserDetails
            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            // 3. Generate JWT
            String token = jwtUtil.generateToken(userDetails);

            // 4. Return JWT + user information
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDto.getEmail())
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }
    }
}
