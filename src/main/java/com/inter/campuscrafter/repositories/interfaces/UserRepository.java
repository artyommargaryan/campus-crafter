package com.inter.campuscrafter.repositories.interfaces;

import com.inter.campuscrafter.entities.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserProfile> findById(String id);
    List<UserProfile> findAll();
    void deleteById(String id);

    UserProfile save(UserProfile userProfile);
}
