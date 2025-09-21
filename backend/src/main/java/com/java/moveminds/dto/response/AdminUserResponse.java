package com.java.moveminds.dto.response;

import com.java.moveminds.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    
    private Integer id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Roles role;
    private boolean isVerified;
    private String avatarUrl;
    private String biography;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private Integer cityId;
    private String cityName;
    
    // Additional fields for admin view
    private Long programCount;
    private Long enrollmentCount;
    private Long studentCount;
    private Long activityCount;
    private Double averageRating;
    private String status;
    private LocalDateTime lastActivityAt;
}