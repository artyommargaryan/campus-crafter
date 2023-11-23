package com.inter.campuscrafter.repositories.interfaces;

import com.inter.campuscrafter.entities.Grade;
import com.inter.campuscrafter.entities.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeRepository {
    Optional<Grade> findById(String id);

    List<Grade> findAll();

    void deleteById(String id);

    Grade save(Grade grade);

    List<Grade> findByAssignmentId(String assignmentId);

    List<Grade> findByStudentId(String studentId);
}
