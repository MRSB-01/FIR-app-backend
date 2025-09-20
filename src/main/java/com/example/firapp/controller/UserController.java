package com.example.firapp.controller;

import com.example.firapp.config.JwtService;
import com.example.firapp.model.User;
import com.example.firapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String photoBase64 = user.getPhoto() != null ? Base64.getEncoder().encodeToString(user.getPhoto()) : null;

        UserProfileResponse response = new UserProfileResponse(
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getFullName(),
                user.getMobileNumber(),
                photoBase64,
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfile(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String mobileNumber,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) String password,
            HttpServletRequest request) throws IOException {

        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (firstName != null) user.setFirstName(firstName);
        if (middleName != null) user.setMiddleName(middleName);
        if (lastName != null) user.setLastName(lastName);
        if (firstName != null || middleName != null || lastName != null) {
            user.setFullName((firstName != null ? firstName : user.getFirstName()) + " " +
                    (middleName != null ? middleName + " " : (user.getMiddleName() != null ? user.getMiddleName() + " " : "")) +
                    (lastName != null ? lastName : user.getLastName()));
        }
        if (mobileNumber != null) user.setMobileNumber(mobileNumber);
        if (photo != null && !photo.isEmpty()) user.setPhoto(photo.getBytes());
        if (password != null) user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
}

class UserProfileResponse {
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private String mobileNumber;
    private String photoBase64;
    private String email;

    public UserProfileResponse(String firstName, String middleName, String lastName, String fullName, String mobileNumber, String photoBase64, String email) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.photoBase64 = photoBase64;
        this.email = email;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public String getMobileNumber() { return mobileNumber; }
    public String getPhotoBase64() { return photoBase64; }
    public String getEmail() { return email; }
}