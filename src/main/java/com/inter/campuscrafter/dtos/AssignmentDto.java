package com.inter.campuscrafter.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDto {
    private String id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Content cannot be blank")
    private String content;
    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDateTime dueDate;
    @NotBlank(message = "Course ID cannot be blank")
    private String courseId;
    @PastOrPresent(message = "Posted date must be in the past or present")
    private LocalDateTime postedDate;
    @Min(value = 0, message = "Max score must be a positive number")
    private int maxScore;
    @NotBlank(message = "Submission format cannot be blank")
    private String submissionFormat;
}
