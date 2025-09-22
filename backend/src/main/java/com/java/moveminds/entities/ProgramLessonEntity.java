package com.java.moveminds.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "program_lesson")
public class ProgramLessonEntity {
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
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Basic
    @Column(name = "video_url")
    private String videoUrl;
    
    @Basic
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Basic
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Basic
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    @Basic
    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_module_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private ProgramModuleEntity programModule;
    
    @JsonIgnore
    @OneToMany(mappedBy = "programLesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ProgramResourceEntity> resources;
}
