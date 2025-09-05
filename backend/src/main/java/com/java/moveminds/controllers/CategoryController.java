package com.java.moveminds.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.java.moveminds.models.dto.CategoryWithSubscriptionDTO;
import com.java.moveminds.models.dto.requests.SubscriptionRequest;
import com.java.moveminds.models.entities.CategoryEntity;
import com.java.moveminds.services.CategoryService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Endpoint for getting all categories
    @GetMapping
    public List<CategoryEntity> getAllCategories() {
        return this.categoryService.listCategories();
    }

    // Endpoint for getting all categories with subscription
    @GetMapping("/subscriptions")
    public ResponseEntity<List<CategoryWithSubscriptionDTO>> getCategoriesWithSubscription(Principal principal) {
        return ResponseEntity.ok(this.categoryService.getCategoriesWithSubscription(principal));
    }

    // Endpoint for adding subscription
    @PostMapping("/subscribe")
    public ResponseEntity<CategoryEntity> subscribeToCategory(Principal principal, @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(this.categoryService.addSubscription(principal, request.getCategoryId()));
    }
}
