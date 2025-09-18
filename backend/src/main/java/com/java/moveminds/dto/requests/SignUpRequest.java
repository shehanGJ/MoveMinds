package com.java.moveminds.dto.requests;

import com.java.moveminds.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String firstName;
    private String lastName;
    private Integer cityId;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;
    private Roles role;
}