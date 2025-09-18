package com.java.moveminds.dto.requests;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private String biography;
    private Integer cityId;
}
