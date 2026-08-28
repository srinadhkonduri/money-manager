package com.moneymanager.controller;

import com.moneymanager.dto.AuthDto;
import com.moneymanager.dto.ProfileDto;
import com.moneymanager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registerProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateToken(@RequestParam String token) {
        boolean isActive = profileService.activateProfile(token);
        if (isActive){
            return ResponseEntity.ok("Profile activated successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String , Object>> login(@RequestBody AuthDto authDto) {
        try {
            if (!profileService.isAccountActive(authDto.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Account is not active. Please activate the account"
                ));
            }
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message",e.getMessage())
            );
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteProfileByEmail(String email){
        profileService.deleteProfileByEMail(email);
        return ResponseEntity.noContent().build();
    }


    // test the JWT
    @GetMapping("/test")
    public String test(){
        return "Test success";
    }
}
