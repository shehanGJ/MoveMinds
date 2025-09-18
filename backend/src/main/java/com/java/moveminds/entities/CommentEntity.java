package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "comment")
public class CommentEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Basic
    @Column(name = "posted_at", nullable = false)
    private Timestamp postedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_program_id", referencedColumnName = "id", nullable = false)
    private FitnessProgramEntity fitnessProgram;

}
