package com.incubytes.sweetshop.Services;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.incubytes.sweetshop.Repository.SweetRepository;
import lombok.RequiredArgsConstructor;
import com.incubytes.sweetshop.Exceptions.UserException;
import com.incubytes.sweetshop.Entities.Sweet;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SweetService {
    private final SweetRepository sweetRepository;

    @Transactional
    public Sweet create(String name, String category, double price, int quantity, String createdBy) {
        if (name == null || name.isBlank()) throw new UserException("Sweet name required");
        if (price < 0) throw new UserException("Price cannot be negative");
        if (quantity < 0) throw new UserException("Quantity cannot be negative");

        Sweet s = new Sweet();
        s.setName(name);
        s.setCategory(category);
        s.setPrice(price);
        s.setQuantity(quantity);
        s.setCreatedBy(createdBy == null ? "system" : createdBy);
        return sweetRepository.save(s);
    }

    @Transactional
    public List<Sweet> listAll() {
        return sweetRepository.findAll();
    }

    @Transactional
    public List<Sweet> search(String name, String category, Double minPrice, Double maxPrice) {
        if (name != null && !name.isBlank()) {
            return sweetRepository.findByNameContainingIgnoreCase(name);
        }
        if (category != null && !category.isBlank()) {
            return sweetRepository.findByCategory(category);
        }
        if (minPrice != null && maxPrice != null) {
            return sweetRepository.findByPriceBetween(minPrice, maxPrice);
        }
        return sweetRepository.findAll();
    }

    @Transactional
    public Sweet update(Long id, String name, String category, Double price, Integer quantity) {
        Sweet s = sweetRepository.findById(id)
                    .orElseThrow(() -> new UserException("Sweet not found with id: " + id, HttpStatus.BAD_REQUEST));

        if (name != null && !name.isBlank()) s.setName(name);
        if (category != null) s.setCategory(category);
        if (price != null) {
            if (price < 0) throw new UserException("Price cannot be negative");
            s.setPrice(price);
        }
        if (quantity != null) {
            if (quantity < 0) throw new UserException("Quantity cannot be negative");
            s.setQuantity(quantity);
        }
        return sweetRepository.save(s);
    }

    @Transactional
    public void delete(Long id) {
        if (sweetRepository.existsById(id)) {
            throw new UserException("Sweet not found with id: " + id);
        }
        sweetRepository.deleteById(id);
    }

    @Transactional
    public Sweet purchase(Long id, int qty) {
        if (qty <= 0) throw new UserException("Quantity to purchase must be positive");

        Sweet s = sweetRepository.findById(id)
                .orElseThrow(() -> new UserException("Sweet not found with id: " + id));

        if (s.getQuantity() < qty) {
            throw new UserException("Not enough stock. Available: " + s.getQuantity());
        }
        s.setQuantity(s.getQuantity() - qty);
        return sweetRepository.save(s);
    }

    @Transactional
    public Sweet restock(Long id, int qty) {
        if (qty <= 0) throw new UserException("Restock quantity must be positive");

        Sweet s = sweetRepository.findById(id)
                .orElseThrow(() -> new UserException("Sweet not found with id: " + id));
        s.setQuantity(s.getQuantity() + qty);
        return sweetRepository.save(s);
    }
}
