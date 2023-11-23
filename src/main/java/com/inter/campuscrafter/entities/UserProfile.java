package com.inter.campuscrafter.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "user_profiles")
@Data
public class UserProfile {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
    private LocalDateTime dateJoined;
    private LocalDateTime lastLogin;
    private String profilePicture;
    private String bio;


    protected UserProfile(UserRole userRole) {
        this.userRole = userRole;
    }
}