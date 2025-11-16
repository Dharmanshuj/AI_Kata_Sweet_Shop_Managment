package com.incubytes.sweetshop.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.incubytes.sweetshop.Entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JWTResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private Role role;
}