package com.incubytes.sweetshop.Util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import com.incubytes.sweetshop.Entities.Role;

import com.incubytes.sweetshop.Entities.User;
@Component
public class Mapper {
    private final PasswordEncoder passwordEncoder;

    public Mapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> mapUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        return claims;
    }

    public User requestToUser(com.incubytes.sweetshop.DTOs.UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        String roleStr = (request.getRole() == null) ? "USER" : request.getRole().toUpperCase();
        user.setRole(Role.valueOf(roleStr));
        return user;
    }
}
