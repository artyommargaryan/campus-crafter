package com.inter.campuscrafter.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private LocalDate startDate;
    private int credits;
    private int enrollmentLimit;
    private CourseStatus status;
}