package com.inter.campuscrafter.repositories.interfaces;

import com.inter.campuscrafter.entities.Student;
import com.inter.campuscrafter.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);
    List<User> findAll();
    void deleteById(String id);

    User save(User user);

    Optional<User> findByEmail(String username);
    Optional<Student> findStudentById(String id);
}
