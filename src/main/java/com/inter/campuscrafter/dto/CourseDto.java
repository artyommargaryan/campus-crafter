package com.inter.campuscrafter.dto;

import com.inter.campuscrafter.entities.CourseStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseDto {
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private LocalDate startDate;
    private int credits;
    private int enrollmentLimit;
    private CourseStatus status;
}
