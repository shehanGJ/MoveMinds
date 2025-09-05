package com.java.moveminds.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.java.moveminds.exceptions.CategoryAlreadyExistsException;
import com.java.moveminds.exceptions.CategoryNotFoundException;
import com.java.moveminds.exceptions.UserNotFoundException;
import com.java.moveminds.models.dto.CategoryWithSubscriptionDTO;
import com.java.moveminds.models.dto.requests.CategoryRequest;
import com.java.moveminds.models.entities.CategoryEntity;
import com.java.moveminds.models.entities.SubscriptionEntity;
import com.java.moveminds.models.entities.UserEntity;
import com.java.moveminds.repositories.CategoryEntityRepository;
import com.java.moveminds.repositories.SubscriptionEntityRepository;
import com.java.moveminds.repositories.UserEntityRepository;
import com.java.moveminds.services.CategoryService;
import com.java.moveminds.services.LogService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryEntityRepository categoryRepository;
    private final UserEntityRepository userRepository;
    private final SubscriptionEntityRepository subscriptionRepository;
    private final LogService logService;

    @Override
    @Transactional
    public CategoryEntity addCategory(CategoryRequest categoryRequest) {

        Optional<CategoryEntity> existingCategory = categoryRepository.findByName(categoryRequest.getName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("A category with the name '" + categoryRequest.getName() + "' already exists.");
        }

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryRequest.getName());
        categoryEntity.setDescription(categoryRequest.getDescription());
        categoryRepository.saveAndFlush(categoryEntity);

        logService.log(null,"Dodavanje kategorije");

        return categoryEntity;
    }

    @Override
    public List<CategoryEntity> listCategories() {

        logService.log(null,"Pregled kategorija");

        return categoryRepository.findAll();
    }

    @Override
    public List<CategoryWithSubscriptionDTO> getCategoriesWithSubscription(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<SubscriptionEntity> subscriptions = this.subscriptionRepository.findAllByUser(user);
        List<CategoryEntity> categories = this.categoryRepository.findAll();

        Set<Integer> subscribedCategoryIds = subscriptions
                .stream()
                .map(subscription -> subscription.getCategory().getId())
                .collect(Collectors.toSet());

        logService.log(principal,"Pregled kategorija sa pretplatom");


        return categories.stream()
                .map(category -> {
                    CategoryWithSubscriptionDTO dto = new CategoryWithSubscriptionDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setDescription(category.getDescription());
                    dto.setSubscribed(subscribedCategoryIds.contains(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryEntity addSubscription(Principal principal, Integer categoryId) {
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Kategorija sa id-em '" + categoryId + "' ne postoji."));

        Boolean isSubscribed = subscriptionRepository.existsByUserAndCategory(user, category);

        if (isSubscribed) {
            subscriptionRepository.deleteByUserAndCategory(user, category);
        } else {
            SubscriptionEntity subscription = new SubscriptionEntity();
            subscription.setUser(user);
            subscription.setCategory(category);
            subscriptionRepository.saveAndFlush(subscription);
        }

        logService.log(principal,"Pretplata na kategoriju");

        return category;
    }
}
