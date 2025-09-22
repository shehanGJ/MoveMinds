package com.java.moveminds.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramModuleResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProgramLessonResponse> lessons;
}
