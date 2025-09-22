package com.java.moveminds.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramResourceResponse {
    private Integer id;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
