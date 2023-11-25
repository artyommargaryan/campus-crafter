package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.repositories.interfaces.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> getAllCourses(Optional<String> status, Optional<String> teacherId) {
        if (status.isPresent() && teacherId.isPresent()) {
            return courseRepository.findAllByStatusAndTeacherId(status.get(), teacherId.get());
        }

        if (status.isPresent()) {
            return courseRepository.findAllByStatus(status.get());
        }

        if (teacherId.isPresent()) {
            return courseRepository.findAllByTeacherId(teacherId.get());
        }

        return courseRepository.findAll();

    }

    public Course getCourseById(String id) {
        Optional<Course> allCourses = courseRepository.findById(id);
        return allCourses.orElse(null);
    }

    public Course createCourse(Course course, User user) {
        if (user.hasRole(UserRole.TEACHER)) {
            course.setTeacherId(user.getId());
        }
        return courseRepository.save(course);
    }

    public Course updateCourseById(String id, Course updatedCourse, User user) {
        Optional<Course> courseById = courseRepository.findById(id);

        return courseById.map(course -> {
                    isAuthorized(updatedCourse, user, "Not authorized to update this course.");

                    course.setTitle(updatedCourse.getTitle());
                    course.setDescription(updatedCourse.getDescription());
                    course.setTeacherId(updatedCourse.getTeacherId());
                    course.setStartDate(updatedCourse.getStartDate());
                    course.setCredits(updatedCourse.getCredits());
                    course.setEnrollmentLimit(updatedCourse.getEnrollmentLimit());
                    course.setStatus(updatedCourse.getStatus());

                    return courseRepository.save(course);
                }
        ).orElse(null);
    }

    void isAuthorized(Course updatedCourse, User user, String message) {
        if (!updatedCourse.getTeacherId().equals(user.getId()) && !user.hasRole(UserRole.ADMIN)) {
            throw new AccessDeniedException(message);
        }
    }

    public void isAuthorized(String courseId, User user, String message) {
        courseRepository.findById(courseId).ifPresent(updatedCourse -> {
            if (!updatedCourse.getTeacherId().equals(user.getId()) && !user.hasRole(UserRole.ADMIN)) {
                throw new AccessDeniedException(message);
            }
        });
    }

    public void deleteCourse(String id, User user) {
        Optional<Course> courseById = courseRepository.findById(id);
        courseById.ifPresent(course -> {
            isAuthorized(course, user, "Not authorized to update this course.");
            courseRepository.deleteById(id);
        });
    }

    public boolean courseExists(String courseId) {
        return courseRepository.findById(courseId).isPresent();
    }
}