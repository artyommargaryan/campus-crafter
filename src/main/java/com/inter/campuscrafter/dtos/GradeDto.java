package com.inter.campuscrafter.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class GradeDto {
    @Id
    private String id;
    @NotBlank(message = "Student ID cannot be blank")
    private String studentId;
    @NotBlank(message = "Assignment ID cannot be blank")
    private String assignmentId;
    @Min(value = 0, message = "Score must be a positive number")
    private int score;
    private String feedback;
    @PastOrPresent(message = "Submission date must be in the past or present")
    private LocalDateTime submissionDate;
}
