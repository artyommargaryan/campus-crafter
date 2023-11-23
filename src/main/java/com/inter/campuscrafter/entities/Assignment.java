package com.inter.campuscrafter.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "assignments")
public class Assignment {
    @Id
    private String id;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private String courseId;
    private LocalDateTime postedDate;
    private int maxScore;
    private String submissionFormat;
}
