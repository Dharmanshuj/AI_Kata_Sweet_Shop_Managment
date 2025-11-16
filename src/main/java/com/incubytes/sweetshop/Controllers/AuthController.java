package com.incubytes.sweetshop.Controllers;


import com.incubytes.sweetshop.Exceptions.UserException;
import com.incubytes.sweetshop.Repository.UserRepository;
import com.incubytes.sweetshop.Services.AuthService;
import com.incubytes.sweetshop.Entities.User;
import com.incubytes.sweetshop.DTOs.JWTResponse;
import com.incubytes.sweetshop.DTOs.LoginRequest;
import com.incubytes.sweetshop.DTOs.UserRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = authService.authenticateUserAndGenerateToken(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserException("User not found after authentication", HttpStatus.BAD_REQUEST));
        return ResponseEntity.ok(new JWTResponse(
                jwt,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRequest userRequest) {
        User newUser = authService.registerUser(userRequest);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

}
