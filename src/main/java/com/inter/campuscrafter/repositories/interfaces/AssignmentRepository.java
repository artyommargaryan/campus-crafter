package com.inter.campuscrafter.repositories.interfaces;

import com.inter.campuscrafter.entities.Assignment;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository {
    Optional<Assignment> findById(String id);
    List<Assignment> findAll();
    void deleteById(String id);

    Assignment save(Assignment assignment);
    List<Assignment> findByCourseId(String courseId);
}
