package com.inter.campuscrafter.services;

import com.inter.campuscrafter.dto.CourseDto;
import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.repositories.interfaces.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseDto> getAllCourses(Optional<String> status, Optional<String> teacherId) {
        if (status.isPresent() && teacherId.isPresent()) {
            List<Course> coursesByStatusAndTeacherId = courseRepository.findAllByStatusAndTeacherId(status.get(), teacherId.get());
            return coursesByStatusAndTeacherId.stream().map(this::mapCourseToCourseDto).toList();
        } else if (status.isPresent()) {
            List<Course> coursesByStatus = courseRepository.findAllByStatus(status.get());
            return coursesByStatus.stream().map(this::mapCourseToCourseDto).toList();
        } else if (teacherId.isPresent()) {
            List<Course> coursesByTeacherId = courseRepository.findAllByTeacherId(teacherId.get());
            return coursesByTeacherId.stream().map(this::mapCourseToCourseDto).toList();
        } else {
            List<Course> allCourses = courseRepository.findAll();
            return allCourses.stream().map(this::mapCourseToCourseDto).toList();
        }

    }

    public CourseDto getCourseById(String id) {
        Optional<Course> allCourses = courseRepository.findById(id);
        return allCourses.map(this::mapCourseToCourseDto).orElse(null);
    }

    public CourseDto createCourse(Course course) {
        Course save = courseRepository.save(course);
        return mapCourseToCourseDto(save);
    }

    private CourseDto mapCourseToCourseDto(Course course) {
        return new CourseDto()
                .setId(course.getId())
                .setTitle(course.getTitle())
                .setDescription(course.getDescription())
                .setTeacherId(course.getTeacherId())
                .setStartDate(course.getStartDate())
                .setCredits(course.getCredits())
                .setEnrollmentLimit(course.getEnrollmentLimit())
                .setStatus(course.getStatus());
    }
}