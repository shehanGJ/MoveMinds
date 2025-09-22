package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_program_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgramProgressEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private FitnessProgramEntity fitnessProgram;
    
    @Column(name = "total_lessons", nullable = false)
    private Integer totalLessons = 0;
    
    @Column(name = "completed_lessons", nullable = false)
    private Integer completedLessons = 0;
    
    @Column(name = "progress_percentage", nullable = false)
    private Double progressPercentage = 0.0;
    
    @Column(name = "total_watch_time_seconds")
    private Integer totalWatchTimeSeconds = 0;
    
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "is_program_completed", nullable = false)
    private Boolean isProgramCompleted = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Helper method to update progress
    public void updateProgress(Integer totalLessons, Integer completedLessons) {
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.progressPercentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100.0 : 0.0;
        this.lastAccessedAt = LocalDateTime.now();
        
        // Check if program is completed
        if (completedLessons.equals(totalLessons) && totalLessons > 0) {
            this.isProgramCompleted = true;
            this.completedAt = LocalDateTime.now();
        }
    }
    
    // Helper method to add watch time
    public void addWatchTime(Integer seconds) {
        this.totalWatchTimeSeconds = (this.totalWatchTimeSeconds != null ? this.totalWatchTimeSeconds : 0) + seconds;
        this.lastAccessedAt = LocalDateTime.now();
    }
}
