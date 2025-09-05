package com.java.moveminds.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "log")
public class LogEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "user")
    private String user;
    @Basic
    @Column(name = "action")
    private String action;
    @Basic
    @Column(name = "timestamp")
    private Timestamp timestamp;

}