package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "program_resource")
public class ProgramResourceEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Basic
    @Column(name = "title", nullable = false)
    private String title;
    
    @Basic
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Basic
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Basic
    @Column(name = "file_type", nullable = false)
    private String fileType; // PDF, DOC, VIDEO, IMAGE, etc.
    
    @Basic
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Basic
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_lesson_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private ProgramLessonEntity programLesson;
}
