package com.inter.campuscrafter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private String id;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private String courseId;
    private LocalDateTime postedDate;
    private int maxScore;
    private String submissionFormat;
}
