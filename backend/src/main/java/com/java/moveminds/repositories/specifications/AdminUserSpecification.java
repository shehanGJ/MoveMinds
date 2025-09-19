package com.java.moveminds.repositories.specifications;

import com.java.moveminds.entities.UserEntity;
import com.java.moveminds.enums.Roles;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminUserSpecification {
    
    public static Specification<UserEntity> hasRole(Roles role) {
        return (root, query, criteriaBuilder) -> 
            role != null ? criteriaBuilder.equal(root.get("role"), role) : null;
    }
    
    public static Specification<UserEntity> hasSearchTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(searchTerm)) {
                return null;
            }
            
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            
            Predicate firstNamePredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("firstName")), likePattern);
            Predicate lastNamePredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("lastName")), likePattern);
            Predicate usernamePredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("username")), likePattern);
            Predicate emailPredicate = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")), likePattern);
            
            return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, 
                                    usernamePredicate, emailPredicate);
        };
    }
    
    public static Specification<UserEntity> isActivated(Boolean isActivated) {
        return (root, query, criteriaBuilder) -> 
            isActivated != null ? criteriaBuilder.equal(root.get("isActivated"), isActivated) : null;
    }
    
    
    public static Specification<UserEntity> hasCity(Integer cityId) {
        return (root, query, criteriaBuilder) -> 
            cityId != null ? criteriaBuilder.equal(root.get("city").get("id"), cityId) : null;
    }
    
    public static Specification<UserEntity> hasPrograms() {
        return (root, query, criteriaBuilder) -> {
            Join<UserEntity, Object> programsJoin = root.join("fitnessPrograms", JoinType.LEFT);
            return criteriaBuilder.isNotNull(programsJoin);
        };
    }
    
    public static Specification<UserEntity> hasEnrollments() {
        return (root, query, criteriaBuilder) -> {
            Join<UserEntity, Object> enrollmentsJoin = root.join("userPrograms", JoinType.LEFT);
            return criteriaBuilder.isNotNull(enrollmentsJoin);
        };
    }
    
    public static Specification<UserEntity> buildSpecification(
            Roles role, String searchTerm, Boolean isActivated, 
            LocalDateTime createdAfter, LocalDateTime createdBefore, 
            Integer cityId, Boolean hasPrograms, Boolean hasEnrollments) {
        
        Specification<UserEntity> spec = Specification.where(hasRole(role))
                .and(hasSearchTerm(searchTerm))
                .and(isActivated(isActivated))
                .and(hasCity(cityId))
                .and(hasPrograms != null && hasPrograms ? hasPrograms() : null)
                .and(hasEnrollments != null && hasEnrollments ? hasEnrollments() : null);
        
        System.out.println("AdminUserSpecification - role: " + role + ", searchTerm: " + searchTerm + ", isActivated: " + isActivated);
        
        return spec;
    }
}
