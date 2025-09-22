package com.java.moveminds.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramStatsResponse {
    private Integer programId;
    private String programName;
    private long totalStudents;
    private long activeStudents;
    private long totalEnrollments;
    private long activeEnrollments;
    private long completedEnrollments;
}
