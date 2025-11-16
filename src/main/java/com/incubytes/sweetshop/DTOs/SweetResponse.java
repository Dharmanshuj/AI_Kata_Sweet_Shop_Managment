package com.incubytes.sweetshop.DTOs;
import com.incubytes.sweetshop.Entities.Sweet;

public record SweetResponse(Long id, String name, String category, double price, int quantity) {
    public static SweetResponse fromEntity(Sweet s) {
        return new SweetResponse(s.getId(), s.getName(), s.getCategory(), s.getPrice(), s.getQuantity());
    }
}