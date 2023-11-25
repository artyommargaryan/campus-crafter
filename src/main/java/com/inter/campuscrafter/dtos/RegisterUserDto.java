package com.inter.campuscrafter.dtos;

import com.inter.campuscrafter.entities.UserRole;
import lombok.Data;

@Data
public class RegisterUserDto {
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}
