package com.java.moveminds.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.java.moveminds.models.enums.DifficultyLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "fitness_program")
public class FitnessProgramEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false)
    private DifficultyLevel difficultyLevel;
    @Basic
    @Column(name = "duration", nullable = false)
    private Integer duration;
    @Basic
    @Column(name = "price", nullable = false, precision = 2)
    private BigDecimal price;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Basic
    @Column(name = "youtube_url")
    private String youtubeUrl;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramImageEntity> programImages;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private LocationEntity location;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgram", cascade = CascadeType.ALL)
    private List<ProgramAttributeEntity> programAttributes;
    @JsonIgnore
    @OneToMany(mappedBy = "fitnessProgramByProgramId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProgramEntity> userPrograms;

}

