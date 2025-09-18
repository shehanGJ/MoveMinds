package com.java.moveminds.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.java.moveminds.entities.CategoryEntity;
import com.java.moveminds.entities.SubscriptionEntity;
import com.java.moveminds.entities.UserEntity;

import java.util.List;

@Repository
public interface SubscriptionEntityRepository extends JpaRepository<SubscriptionEntity, Integer> {
    List<SubscriptionEntity> findAllByUser(UserEntity user);
    List<SubscriptionEntity> findAllByCategory(CategoryEntity category);
    Boolean existsByUserAndCategory(UserEntity user, CategoryEntity category);
    void deleteByUserAndCategory(UserEntity user, CategoryEntity category);
}
