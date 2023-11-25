package com.inter.campuscrafter.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserDto {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email length should be at most 100 characters")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;}
