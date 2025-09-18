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
    private boolean isActivated;
    private String avatarUrl;
    private String biography;
    private String cityName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private int programCount;
    private int enrollmentCount;
    private int activityCount;
}
