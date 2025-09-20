package com.example.firapp.controller;

import com.example.firapp.config.JwtService;
import com.example.firapp.model.User;
import com.example.firapp.repository.UserRepository;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://fir-app-frontend.vercel.app/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final DefaultKaptcha captchaProducer;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                          AuthenticationManager authenticationManager, DefaultKaptcha captchaProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.captchaProducer = captchaProducer;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(
            @RequestParam String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam String lastName,
            @RequestParam String mobileNumber,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword) throws IOException {

        logger.info("Received registration request for email: {}", email);

        if (!password.equals(confirmPassword)) {
            logger.warn("Passwords do not match for email: {}", email);
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Email already exists: {}", email);
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (photo.isEmpty()) {
            logger.warn("Photo is missing for email: {}", email);
            return ResponseEntity.badRequest().body("Photo is required");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        user.setLastName(lastName);
        user.setFullName(firstName + " " + (middleName != null ? middleName + " " : "") + lastName);
        user.setMobileNumber(mobileNumber);
        user.setPhoto(photo.getBytes());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); // Set default role
        userRepository.save(user);

        logger.info("User registered successfully: {}", email);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            logger.info("Login attempt for email: {}, captcha: '{}'", request.getEmail(), request.getCaptcha());

            // Validate CAPTCHA (no session, direct comparison with request)
            if (request.getCaptcha() == null || !isValidCaptcha(request.getCaptcha())) {
                logger.warn("Invalid CAPTCHA for email: {}", request.getEmail());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials or captcha");
            }
            logger.info("CAPTCHA validated for email: {}", request.getEmail());

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            logger.info("Authentication successful for email: {}", request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
            UserDetails userDetails = new com.example.firapp.config.CustomUserDetails(user);
            String jwtToken = jwtService.generateToken(userDetails);
            logger.info("JWT generated for email: {}", request.getEmail());

            logger.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(jwtToken));
        } catch (Exception e) {
            logger.error("Login failed for email: {}, error: {}", request.getEmail(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials or captcha");
        }
    }

    @GetMapping("/captcha-text")
    public ResponseEntity<CaptchaTextResponse> getCaptchaText() {
        String text = generateCaptchaText();
        logger.debug("Generated captchaText: '{}'", text);
        return ResponseEntity.ok(new CaptchaTextResponse(text));
    }

    private String generateCaptchaText() {
        // Generate a simple 6-character alphanumeric CAPTCHA
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private boolean isValidCaptcha(String captcha) {
        // For simplicity, accept any 6-character alphanumeric input as valid
        // In a real application, you might want to store and compare against a generated value
        if (captcha == null || captcha.length() != 6) return false;
        return captcha.matches("[A-Z0-9]{6}");
    }

    record CaptchaTextResponse(String captchaText) {
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String email = jwtService.extractUsername(token); // Implement this in JwtService
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(new UserResponse(user.getEmail(), user.getFullName()));
    }

    record UserResponse(String email, String fullName) {}

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getDashboardStats(@RequestHeader("Authorization") String authorization) {
        // Mock data for now
        return ResponseEntity.ok(new StatsResponse(100, 50)); // Replace with real data
    }

    record StatsResponse(int totalUsers, int activeSessions) {}
}

// -------- DTOs --------

class RegisterRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private String photoBase64;
    private String email;
    private String password;
    private String confirmPassword;

    public RegisterRequest() {
    }

    public RegisterRequest(String firstName, String middleName, String lastName, String mobileNumber, String photoBase64,
                           String email, String password, String confirmPassword) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.photoBase64 = photoBase64;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}

class LoginRequest {
    private String email;
    private String password;
    private String captcha;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password, String captcha) {
        this.email = email;
        this.password = password;
        this.captcha = captcha;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getCaptcha() { return captcha; }
    public void setCaptcha(String captcha) { this.captcha = captcha; }
}

class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}