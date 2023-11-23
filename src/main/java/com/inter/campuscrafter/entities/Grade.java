package com.inter.campuscrafter.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "grades")
public class Grade {
    @Id
    private String id;
    private String studentId;
    private String assignmentId;
    private int score;
    private String feedback;
    private LocalDateTime submissionDate;
}
