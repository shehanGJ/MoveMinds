package com.java.moveminds.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.java.moveminds.dto.CategoryWithSubscriptionDTO;
import com.java.moveminds.dto.requests.CategoryRequest;
import com.java.moveminds.entities.CategoryEntity;

import java.security.Principal;
import java.util.List;

@Service
public interface CategoryService {
    @Transactional
    CategoryEntity addCategory(CategoryRequest categoryRequest);
    List<CategoryEntity> listCategories();
    List<CategoryWithSubscriptionDTO> getCategoriesWithSubscription(Principal principal);
    @Transactional
    CategoryEntity addSubscription(Principal principal, Integer categoryId);
}
