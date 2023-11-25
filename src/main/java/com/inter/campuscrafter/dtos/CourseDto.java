package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.CourseStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseDto {
    private String id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotBlank(message = "Teacher ID cannot be blank")
    private String teacherId;
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;
    @Min(value = 0, message = "Credits must be a positive number")
    private int credits;
    @Min(value = 0, message = "Enrollment limit must be a positive number")
    private int enrollmentLimit;
    @NotNull(message = "Status cannot be null")
    private CourseStatus status;
}
