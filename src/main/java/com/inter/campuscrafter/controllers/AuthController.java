package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.LoginUserDto;
import com.inter.campuscrafter.dtos.RegisterUserDto;
import com.inter.campuscrafter.dtos.UserDto;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.responses.LoginResponse;
import com.inter.campuscrafter.services.JwtService;
import com.inter.campuscrafter.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Auth Controller")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(description = "Allows admin to create a new user account. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only admins can register new users")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegisterUserDto newUserDto) {
        User newUser = mapRegisterUserDtoToUser(newUserDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Encode the password
        User createdUser = userService.createUser(newUser);
        UserDto createdUserDto = mapUserToUserDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @PostMapping("/login")
    @Operation(description = "Authenticates the user's credentials and returns a JWT token upon successful login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")})
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        User loginUser = mapLoginUserDtoToUser(loginUserDto);
        User authenticatedUser = userService.authenticate(loginUser);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    private UserDto mapUserToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapRegisterUserDtoToUser(RegisterUserDto newUserDto) {
        return modelMapper.map(newUserDto, User.class);
    }

    private User mapLoginUserDtoToUser(LoginUserDto loginUserDto) {
        return modelMapper.map(loginUserDto, User.class);
    }

}
