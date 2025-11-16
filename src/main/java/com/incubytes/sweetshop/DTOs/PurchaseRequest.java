package com.incubytes.sweetshop.DTOs;

import jakarta.validation.constraints.Min;

public record PurchaseRequest(@Min(1) int qty) {}

