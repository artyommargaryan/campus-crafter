package com.inter.campuscrafter.config;

import com.inter.campuscrafter.entities.Student;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.repositories.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String defaultAdminEmail = "default.admin@example.com";
        if (userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            User defaultUser = new User();
            defaultUser.setName("Default Admin");
            defaultUser.setEmail(defaultAdminEmail);
            defaultUser.setPassword(passwordEncoder.encode("password"));
            defaultUser.setUserRole(UserRole.ADMIN);
            defaultUser.setDateJoined(LocalDateTime.now());
            userRepository.save(defaultUser);
        }

        String defaultTeacherEmail = "default.teacher@example.com";
        if (userRepository.findByEmail(defaultTeacherEmail).isEmpty()) {
            User defaultUser = new User();
            defaultUser.setName("Default Teacher");
            defaultUser.setEmail(defaultTeacherEmail);
            defaultUser.setPassword(passwordEncoder.encode("password"));
            defaultUser.setUserRole(UserRole.TEACHER);
            defaultUser.setDateJoined(LocalDateTime.now());
            userRepository.save(defaultUser);
        }

        String defaultStudentEmail = "default.student@example.com";
        if (userRepository.findByEmail(defaultStudentEmail).isEmpty()) {
            Student defaultUser = new Student();
            defaultUser.setName("Default Student");
            defaultUser.setEmail(defaultStudentEmail);
            defaultUser.setPassword(passwordEncoder.encode("password"));
            defaultUser.setUserRole(UserRole.STUDENT);
            defaultUser.setDateJoined(LocalDateTime.now());
            defaultUser.setCourseIds(List.of("655f76a5c75dc0165ef4007d"));
            userRepository.save(defaultUser);
        }
    }
}
