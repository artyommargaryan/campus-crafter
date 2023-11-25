package com.inter.campuscrafter.services;

import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.Student;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.exceptions.UserNotFoundException;
import com.inter.campuscrafter.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean enrolledInCourse(String userId, String courseId) {
        Student student = userRepository.findStudentById(userId)
                .orElseThrow(() -> new IllegalStateException("Student not found with ID: " + userId));

        return student.getCourseIds()
                .stream()
                .anyMatch(courseId::equals);
    }

    public boolean enrolledInAnyCourse(String studentId, List<Course> allCourses) {
        return !allCourses.stream()
                .filter(course -> enrolledInCourse(studentId, course.getId()))
                .toList()
                .isEmpty();
    }

    public boolean userExists(String userId) {
        return userRepository.findById(userId).isPresent();
    }

    public User updateUser(String userId, User userProfile) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setName(userProfile.getName());
                    user.setEmail(userProfile.getEmail());
                    user.setUserRole(userProfile.getUserRole());
                    user.setBio(userProfile.getBio());

                    return userRepository.save(user);
                })
                .orElse(null);
    }

    public User createUser(User newUser) {
        newUser.setDateJoined(LocalDateTime.now());
        return userRepository.save(newUser);
    }


    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public User authenticate(User loginUser) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(),
                        loginUser.getPassword()
                )
        );

        return userRepository.findByEmail(loginUser.getEmail()).map(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException("User not found for login"));
    }
}
