package com.incubytes.sweetshop.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sweets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sweet {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category;

    private double price;

    private int quantity;

    @Column(nullable = false)
    private String createdBy;
}
