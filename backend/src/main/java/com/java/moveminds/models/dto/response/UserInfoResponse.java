package com.java.moveminds.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private String biography;
    private Integer cityId;
}
