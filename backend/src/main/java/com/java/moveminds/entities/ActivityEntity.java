package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

@Data
@Entity
@Table(name = "activity")
public class ActivityEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "activity_type", nullable = false)
    private String activityType;
    @Basic
    @Column(name = "duration", nullable = false)
    private Integer duration;
    @Basic
    @Column(name = "intensity", nullable = false)
    private String intensity;
    @Basic
    @Column(name = "result", nullable = false)
    private BigDecimal result;
    @Basic
    @Column(name = "log_date", nullable = false)
    private Date logDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

}
