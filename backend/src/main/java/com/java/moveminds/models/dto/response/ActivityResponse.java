package com.java.moveminds.models.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class ActivityResponse {
    private Integer id;
    private Integer userId;
    private String activityType;
    private Integer duration;
    private String intensity;
    private BigDecimal result;
    private Date logDate;
}
