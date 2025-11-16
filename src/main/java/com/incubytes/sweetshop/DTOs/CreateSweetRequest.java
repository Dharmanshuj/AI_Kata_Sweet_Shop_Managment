package com.incubytes.sweetshop.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateSweetRequest(
            @NotBlank String name,
            String category,
            @Min(0) double price,
            @Min(0) int quantity) {}
