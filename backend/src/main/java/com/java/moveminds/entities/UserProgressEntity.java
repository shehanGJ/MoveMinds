package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private FitnessProgramEntity fitnessProgram;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private ProgramLessonEntity lesson;
    
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "watch_time_seconds")
    private Integer watchTimeSeconds = 0;
    
    @Column(name = "last_watched_at")
    private LocalDateTime lastWatchedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Helper method to mark as completed
    public void markAsCompleted() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.lastWatchedAt = LocalDateTime.now();
    }
    
    // Helper method to update watch time
    public void updateWatchTime(Integer additionalSeconds) {
        this.watchTimeSeconds = (this.watchTimeSeconds != null ? this.watchTimeSeconds : 0) + additionalSeconds;
        this.lastWatchedAt = LocalDateTime.now();
    }
}
