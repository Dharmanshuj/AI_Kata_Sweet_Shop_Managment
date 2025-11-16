package com.incubytes.sweetshop.Services;

import com.incubytes.sweetshop.DTOs.LoginRequest;
import com.incubytes.sweetshop.DTOs.UserRequest;
import com.incubytes.sweetshop.Entities.User;
import com.incubytes.sweetshop.Exceptions.UserException;
import com.incubytes.sweetshop.Repository.UserRepository;
import com.incubytes.sweetshop.Util.Mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtUtil;

    private final Mapper mapper;
    @Transactional
    public User registerUser(UserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(
                "User with email " + request.getEmail() + "already exists.", HttpStatus.BAD_REQUEST);
        }

        User user = mapper.requestToUser(request);
        return userRepository.save(user);
    }

    public String authenticateUserAndGenerateToken(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UserException("User not found with email: " + loginRequest.email(), HttpStatus.BAD_REQUEST));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (AuthenticationException e) {
            throw new UserException("Invalid Username or Password", HttpStatus.UNAUTHORIZED);
        }
        return jwtUtil.generateToken(user.getEmail());
    }
}
