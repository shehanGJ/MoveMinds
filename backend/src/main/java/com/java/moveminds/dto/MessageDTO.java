package com.java.moveminds.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MessageDTO {
    private Integer id;
    private Integer senderId;
    private Integer recipientId;
    private String subject;
    private String content;
    private Timestamp sentAt;
    private Timestamp readAt;
}
