package com.inter.campuscrafter.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class GradeDto {
    @Id
    private String id;
    private String studentId;
    private String assignmentId;
    private int score;
    private String feedback;
    private LocalDateTime submissionDate;
}
