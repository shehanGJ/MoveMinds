package com.java.moveminds.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAnalyticsResponse {
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal dailyRevenue;
    private Double revenueGrowthRate;
    
    private List<RevenueDataPoint> dailyRevenueData;
    private List<RevenueDataPoint> weeklyRevenueData;
    private List<RevenueDataPoint> monthlyRevenueData;
    
    private List<RevenueSource> revenueBySource;
    private List<RevenueByCategory> revenueByCategory;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDataPoint {
        private String date;
        private BigDecimal amount;
        private Long transactionCount;
        private Double growthRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueSource {
        private String source;
        private BigDecimal amount;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByCategory {
        private String category;
        private BigDecimal amount;
        private Long programCount;
        private Double percentage;
    }
}
