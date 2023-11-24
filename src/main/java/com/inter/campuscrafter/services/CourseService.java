package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.UserProfile;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.repositories.interfaces.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Course createCourse(Course course, UserProfile userProfile) {
        if (userProfile.hasRole(UserRole.TEACHER)) {
            course.setTeacherId(userProfile.getId());
        }
        return courseRepository.save(course);
    }

    public Course updateCourseById(String id, Course updatedCourse, UserProfile userProfile) {
        Optional<Course> courseById = courseRepository.findById(id);

        return courseById.map(course -> {
                    isAuthorized(updatedCourse, userProfile, "Not authorized to update this course.");

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

    void isAuthorized(Course updatedCourse, UserProfile userProfile, String message) {
        if (!updatedCourse.getTeacherId().equals(userProfile.getId()) && !userProfile.hasRole(UserRole.ADMIN)) {
            throw new AccessDeniedException(message);
        }
    }

    public void isAuthorized(String courseId, UserProfile userProfile, String message) {
        courseRepository.findById(courseId).ifPresent(updatedCourse -> {
            if (!updatedCourse.getTeacherId().equals(userProfile.getId()) && !userProfile.hasRole(UserRole.ADMIN)) {
                throw new AccessDeniedException(message);
            }
        });
    }

    public void deleteCourse(String id, UserProfile userProfile) {
        Optional<Course> courseById = courseRepository.findById(id);
        courseById.ifPresent(course -> {
            isAuthorized(course, userProfile, "Not authorized to update this course.");
            courseRepository.deleteById(id);
        });
    }

    public boolean courseExists(String courseId) {
        return courseRepository.findById(courseId).isPresent();
    }
}