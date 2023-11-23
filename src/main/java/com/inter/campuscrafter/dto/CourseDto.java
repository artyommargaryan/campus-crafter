package com.inter.campuscrafter.dto;

import com.inter.campuscrafter.entities.CourseStatus;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
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
