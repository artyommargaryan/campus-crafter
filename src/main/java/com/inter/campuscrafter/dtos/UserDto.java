package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private String id;
    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Name length should be between 2 and 50 characters")
    private String name;
    @NotBlank
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email length should be at most 100 characters")
    private String email;
    @NotNull(message = "User role cannot be null")
    private UserRole userRole;
    private LocalDateTime dateJoined;
    private LocalDateTime lastLogin;
    private String bio;
}
