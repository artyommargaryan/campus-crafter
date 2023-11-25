package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String id;
    private String name;
    private String email;
    private UserRole userRole;
    private LocalDateTime dateJoined;
    private LocalDateTime lastLogin;
    private String bio;
}
