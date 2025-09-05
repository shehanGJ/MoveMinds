package com.java.moveminds.models.dto;

import lombok.Data;

@Data
public class CategoryWithSubscriptionDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean subscribed;
}
