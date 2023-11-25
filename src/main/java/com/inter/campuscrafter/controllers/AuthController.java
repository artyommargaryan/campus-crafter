package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.LoginUserDto;
import com.inter.campuscrafter.dtos.RegisterUserDto;
import com.inter.campuscrafter.dtos.UserDto;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.responses.LoginResponse;
import com.inter.campuscrafter.services.JwtService;
import com.inter.campuscrafter.services.UserService;
import io.swagger.annotations.*;
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
@Api(tags = "Authentication", value = "Auth Controller")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ApiOperation(value = "Register a new user", notes = "Allows admin to create a new user account. Requires admin role.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 403, message = "Forbidden - Only admins can register new users")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> registerUser(@ApiParam(value = "User registration data", required = true)
                                                @RequestBody RegisterUserDto newUserDto) {
        User newUser = mapRegisterUserDtoToUser(newUserDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword())); // Encode the password
        User createdUser = userService.createUser(newUser);
        UserDto createdUserDto = mapUserToUserDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @PostMapping("/login")
    @ApiOperation(value = "Authenticate user and return JWT", notes = "Authenticates the user's credentials and returns a JWT token upon successful login.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User authenticated successfully"),
            @ApiResponse(code = 401, message = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<LoginResponse> authenticate(@ApiParam(value = "User login data", required = true)
                                                      @RequestBody LoginUserDto loginUserDto) {
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
