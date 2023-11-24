package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Student;
import com.inter.campuscrafter.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean enrolledInCourse(String userId, String courseId) {
        Student student = userRepository.findStudentById(userId)
                .orElseThrow(() -> new IllegalStateException("Student not found with ID: " + userId));

        return student.getCourseIds()
                .stream()
                .anyMatch(courseId::equals);
    }
}
