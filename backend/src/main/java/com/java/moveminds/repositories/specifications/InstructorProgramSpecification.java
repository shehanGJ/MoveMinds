package com.java.moveminds.repositories.specifications;

import com.java.moveminds.entities.FitnessProgramEntity;
import com.java.moveminds.enums.DifficultyLevel;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InstructorProgramSpecification {
    
    public static Specification<FitnessProgramEntity> belongsToInstructor(Integer instructorId) {
        return (root, query, criteriaBuilder) -> 
            instructorId != null ? criteriaBuilder.equal(root.get("instructor").get("id"), instructorId) : null;
    }
    
    public static Specification<FitnessProgramEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null;
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")), 
                "%" + name.toLowerCase() + "%");
        };
    }
    
    public static Specification<FitnessProgramEntity> hasDifficulty(DifficultyLevel difficulty) {
        return (root, query, criteriaBuilder) -> 
            difficulty != null ? criteriaBuilder.equal(root.get("difficultyLevel"), difficulty) : null;
    }
    
    public static Specification<FitnessProgramEntity> hasCategory(Integer categoryId) {
        return (root, query, criteriaBuilder) -> 
            categoryId != null ? criteriaBuilder.equal(root.get("category").get("id"), categoryId) : null;
    }
    
    public static Specification<FitnessProgramEntity> hasLocation(Integer locationId) {
        return (root, query, criteriaBuilder) -> 
            locationId != null ? criteriaBuilder.equal(root.get("location").get("id"), locationId) : null;
    }
    
    public static Specification<FitnessProgramEntity> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<FitnessProgramEntity> durationBetween(Integer minDuration, Integer maxDuration) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (minDuration != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), minDuration));
            }
            
            if (maxDuration != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("duration"), maxDuration));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<FitnessProgramEntity> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> 
            isActive != null ? criteriaBuilder.equal(root.get("isActive"), isActive) : null;
    }
    
    public static Specification<FitnessProgramEntity> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> 
            date != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date) : null;
    }
    
    public static Specification<FitnessProgramEntity> hasEnrollments() {
        return (root, query, criteriaBuilder) -> {
            Join<FitnessProgramEntity, Object> enrollmentsJoin = root.join("userPrograms", JoinType.LEFT);
            return criteriaBuilder.isNotNull(enrollmentsJoin);
        };
    }
    
    public static Specification<FitnessProgramEntity> hasMinimumEnrollments(Long minEnrollments) {
        return (root, query, criteriaBuilder) -> {
            if (minEnrollments == null || minEnrollments <= 0) {
                return null;
            }
            
            Subquery<Long> enrollmentCountSubquery = query.subquery(Long.class);
            Root<FitnessProgramEntity> subRoot = enrollmentCountSubquery.from(FitnessProgramEntity.class);
            enrollmentCountSubquery.select(criteriaBuilder.count(subRoot.get("userPrograms")))
                    .where(criteriaBuilder.equal(subRoot.get("id"), root.get("id")));
            
            return criteriaBuilder.greaterThanOrEqualTo(enrollmentCountSubquery, minEnrollments);
        };
    }
    
    public static Specification<FitnessProgramEntity> buildSpecification(
            Integer instructorId, String name, DifficultyLevel difficulty, 
            Integer categoryId, Integer locationId, BigDecimal minPrice, 
            BigDecimal maxPrice, Integer minDuration, Integer maxDuration, 
            Boolean isActive, LocalDateTime createdAfter, Long minEnrollments) {
        
        return Specification.where(belongsToInstructor(instructorId))
                .and(hasName(name))
                .and(hasDifficulty(difficulty))
                .and(hasCategory(categoryId))
                .and(hasLocation(locationId))
                .and(priceBetween(minPrice, maxPrice))
                .and(durationBetween(minDuration, maxDuration))
                .and(isActive(isActive))
                .and(createdAfter(createdAfter))
                .and(hasMinimumEnrollments(minEnrollments));
    }
}
