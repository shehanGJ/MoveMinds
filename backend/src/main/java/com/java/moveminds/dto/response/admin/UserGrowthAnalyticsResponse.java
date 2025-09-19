package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGrowthAnalyticsResponse {
    
    private List<GrowthDataPoint> dailyGrowth;
    private List<GrowthDataPoint> weeklyGrowth;
    private List<GrowthDataPoint> monthlyGrowth;
    
    private Long totalGrowth;
    private Double growthRate;
    private String period;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrowthDataPoint {
        private String date;
        private Long newUsers;
        private Long totalUsers;
        private Double growthPercentage;
    }
}
