package com.inter.campuscrafter.repositories.interfaces;

import com.inter.campuscrafter.entities.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(String id);

    List<Course> findAll();

    List<Course> findAllByStatusAndTeacherId(String status, String teacherId);

    List<Course> findAllByStatus(String status);

    void deleteById(String id);

    Course save(Course course);

    List<Course> findAllByTeacherId(String teacherId);
}

