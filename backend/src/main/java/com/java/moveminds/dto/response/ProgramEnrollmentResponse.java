package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramEnrollmentResponse {
    private Integer enrollmentId;
    private Integer userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarUrl;
    private Integer programId;
    private String programName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime enrolledAt;
    private Integer progress;
    private String cityName;
}
