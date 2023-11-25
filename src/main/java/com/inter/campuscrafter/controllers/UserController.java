package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.UserDto;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.services.GradeService;
import com.inter.campuscrafter.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User Controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GradeService gradeService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{userId}")
    @Operation(description = "Fetches the profile of a specific user. Accessible by the user themselves and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this profile")
    })
    @PreAuthorize("#userId == principal.username or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto userDto = mapUserToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}")
    @Operation(description = "Updates the profile for a specific user. Accessible by the user themselves and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this profile")
    })
    @PreAuthorize("#userId == principal.username or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable String userId,
                                                     @RequestBody @Valid UserDto userDto) {
        User user = mapUserDtoToUser(userDto);
        User updatedUser = userService.updateUser(userId, user);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto updatedUserDto = mapUserToUserDto(updatedUser);
        return ResponseEntity.ok(updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    @Operation(description = "Deletes a specific user profile. Accessible by admins only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only admins can delete users")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        User userById = userService.getUserById(userId);
        userService.deleteUser(userId);
        if (userById.hasRole(UserRole.STUDENT)) {
            gradeService.deleteGradesByStudentId(userId);
        }
        return ResponseEntity.ok().build();
    }

    private UserDto mapUserToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapUserDtoToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
