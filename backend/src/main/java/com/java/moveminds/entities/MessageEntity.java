package com.java.moveminds.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "message")
public class MessageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "subject")
    private String subject;
    @Basic
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Basic
    @Column(name = "sent_at", nullable = false)
    private Timestamp sentAt;
    @Basic
    @Column(name = "read_at")
    private Timestamp readAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private UserEntity sender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
    private UserEntity recipient;

}
