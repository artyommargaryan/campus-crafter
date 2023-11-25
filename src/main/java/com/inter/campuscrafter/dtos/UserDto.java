package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "User role cannot be null")
    private UserRole userRole;
    private LocalDateTime dateJoined;
    private LocalDateTime lastLogin;
    private String bio;
}
