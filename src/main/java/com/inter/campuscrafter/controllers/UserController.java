package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.UserDto;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.services.GradeService;
import com.inter.campuscrafter.services.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Api(tags = "User Management", value = "User Controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final GradeService gradeService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{userId}")
    @ApiOperation(value = "Get User Profile", notes = "Fetches the profile of a specific user. Accessible by the user themselves and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user profile"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden - User not authorized to view this profile")
    })
    @PreAuthorize("#userId == principal.username or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserProfile(@ApiParam(value = "Unique ID of the user", required = true)
                                                  @PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto userDto = mapUserToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{userId}")
    @ApiOperation(value = "Update User Profile", notes = "Updates the profile for a specific user. Accessible by the user themselves and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User profile updated successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden - User not authorized to update this profile")
    })
    @PreAuthorize("#userId == principal.username or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUserProfile(@ApiParam(value = "Unique ID of the user", required = true)
                                                     @PathVariable String userId,
                                                     @ApiParam(value = "Updated user data", required = true)
                                                     @RequestBody UserDto userDto) {
        User user = mapUserDtoToUser(userDto);
        User updatedUser = userService.updateUser(userId, user);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto updatedUserDto = mapUserToUserDto(updatedUser);
        return ResponseEntity.ok(updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    @ApiOperation(value = "Delete User", notes = "Deletes a specific user profile. Accessible by admins only.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 403, message = "Forbidden - Only admins can delete users")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@ApiParam(value = "Unique ID of the user to delete", required = true)
                                           @PathVariable String userId) {
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
