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
@Table(name = "program_module")
public class ProgramModuleEntity {
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
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Basic
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_program_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private FitnessProgramEntity fitnessProgram;
    
    @JsonIgnore
    @OneToMany(mappedBy = "programModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<ProgramLessonEntity> lessons;
}
