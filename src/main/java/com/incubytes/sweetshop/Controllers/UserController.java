package com.incubytes.sweetshop.Controllers;
import com.incubytes.sweetshop.DTOs.PurchaseRequest;
import com.incubytes.sweetshop.DTOs.SweetResponse;
import com.incubytes.sweetshop.DTOs.UpdateSweetRequest;
import com.incubytes.sweetshop.Entities.Sweet;
import com.incubytes.sweetshop.Services.SweetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.incubytes.sweetshop.DTOs.CreateSweetRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sweets")
@Validated
public class UserController {
    private final SweetService service;

    private boolean isAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    public ResponseEntity<SweetResponse> create(@Valid @RequestBody CreateSweetRequest req, Authentication authentication) {
        String createdBy = (authentication != null) ? (String) authentication.getPrincipal() : null;
        Sweet s = service.create(req.name(), req.category(), req.price(), req.quantity(), createdBy);
        return ResponseEntity.status(201).body(SweetResponse.fromEntity(s));
    }

    @GetMapping
    public ResponseEntity<List<SweetResponse>> listAll() {
        List<Sweet> sweets = service.listAll();
        var resp = sweets.stream().map(SweetResponse::fromEntity).toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SweetResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Sweet> sweets = service.search(name, category, minPrice, maxPrice);
        var resp = sweets.stream().map(SweetResponse::fromEntity).toList();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SweetResponse> update(@PathVariable Long id, @RequestBody UpdateSweetRequest req, Authentication auth) {
        // Optionally check that the authenticated user is the creator or admin
        // (business rule): currently anyone authenticated can update; restrict if needed.
        Sweet s = service.update(id, req.name(), req.category(), req.price(), req.quantity());
        return ResponseEntity.ok(SweetResponse.fromEntity(s));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<SweetResponse> purchase(@PathVariable Long id, @Valid @RequestBody PurchaseRequest req, Authentication auth) {
        // Authentication may be null if you permit guest purchase — adapt as needed.
        Sweet s = service.purchase(id, req.qty());
        return ResponseEntity.ok(SweetResponse.fromEntity(s));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/restock")
    public ResponseEntity<SweetResponse> restock(@PathVariable Long id, @Valid @RequestBody PurchaseRequest req, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(403).build();
        Sweet s = service.restock(id, req.qty());
        return ResponseEntity.ok(SweetResponse.fromEntity(s));
    }
}
