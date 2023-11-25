package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterUserDto {
    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Name should contain only letters, spaces, apostrophes, hyphens, and dots")
    @Size(min = 2, max = 50, message = "Name length should be between 2 and 50 characters")
    private String name;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;
    @NotNull(message = "User role cannot be null")
    private UserRole userRole;
}
