package com.moneymanager.service;

import com.moneymanager.dto.ProfileDto;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileService(
            ProfileRepository profileRepository,
            EmailService emailService) {

        this.profileRepository = profileRepository;
        this.emailService = emailService;
    }

    public ProfileDto registerProfile(ProfileDto profileDto) {

        // 1. Convert DTO to Entity
        ProfileEntity newProfile = toEntity(profileDto);

        // 2. Generate activation token
        newProfile.setActivationToken(UUID.randomUUID().toString());

        // 3. Save profile to database
        newProfile = profileRepository.save(newProfile);

        // 4. Create activation link
        String activationLink =
                "http://localhost:8080/api/v1.0/activate?token="
                        + newProfile.getActivationToken();

        // 5. Create email
        String subject = "Activate your Money Manager account";

        String body =
                "Hello " + newProfile.getFullName() + ",\n\n"
                        + "Thank you for registering with Money Manager.\n\n"
                        + "Please click the following link to activate your account:\n\n"
                        + activationLink
                        + "\n\n"
                        + "If you did not create this account, please ignore this email.";

        // 6. Send activation email
        emailService.sendEmail(
                newProfile.getEmail(),
                subject,
                body
        );

        // 7. Convert Entity back to DTO. Here newProfile is Entity
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDto profileDto) {

        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(profileDto.getPassword())
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDTO(ProfileEntity profileEntity) {

        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
}