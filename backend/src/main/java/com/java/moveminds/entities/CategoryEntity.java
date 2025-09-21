package com.java.moveminds.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name = "category")
public class CategoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<AttributeEntity> attributes;
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    private List<FitnessProgramEntity> fitnessPrograms;
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<SubscriptionEntity> subscriptions;

}
