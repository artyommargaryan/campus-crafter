package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.GradeDto;
import com.inter.campuscrafter.entities.Grade;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.exceptions.AssignmentNotFoundException;
import com.inter.campuscrafter.exceptions.UserNotFoundException;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
import com.inter.campuscrafter.services.GradeService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@Tag(name = "Grade Management", description = "Grade Controller")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/assignments/{assignmentId}/grades")
    @Operation(description = "Allows teachers or admins to submit grades for a specific assignment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade submitted successfully"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to submit grade for this assignment")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<GradeDto> getGrade(@PathVariable String assignmentId,
                                             @Valid GradeDto gradeDto,
                                             Authentication authentication) {
        if (!assignmentService.assignmentExists(assignmentId)) {
            throw new AssignmentNotFoundException("Assignment " + assignmentId + " not found");
        }

        User principal = (User) authentication.getPrincipal();
        var assignment = assignmentService.getAssignmentById(assignmentId);
        courseService.isAuthorized(assignment.getCourseId(), principal, "Not authorized to add grade to this assignment.");
        Grade grade = mapGradeDtoToGrade(gradeDto);
        Grade savedGrade = gradeService.createGrade(grade, assignmentId);
        GradeDto savedGradeDto = mapGradeToGradeDto(savedGrade);
        return new ResponseEntity<>(savedGradeDto, HttpStatus.CREATED);
    }

    @GetMapping("/students/{studentId}/grades")
    @Operation(description = "Retrieves all grades for a specified student. Accessible by students for their own grades, teachers for students in their courses, and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grades retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these grades")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getAllGradesByStudentId(@PathVariable String studentId,
                                                                  Authentication authentication) {
        if (!userService.userExists(studentId)) {
            throw new UserNotFoundException("Student " + studentId + "not found");
        }

        User principal = (User) authentication.getPrincipal();

        if (principal.hasRole(UserRole.STUDENT) && !principal.getId().equals(studentId)) {
            throw new AccessDeniedException("Not authorized to get this students grades.");
        }

        if (principal.hasRole(UserRole.TEACHER) && !userService.enrolledInAnyCourse(studentId,
                courseService.getAllCourses(Optional.empty(), Optional.of(principal.getId())))) {
            throw new AccessDeniedException("Not authorized to get this students grades.");
        }

        List<Grade> grades = gradeService.getGrades(studentId);
        List<GradeDto> gradeDtos = grades.stream().map(this::mapGradeToGradeDto).toList();
        return ResponseEntity.ok(gradeDtos);
    }

    private GradeDto mapGradeToGradeDto(Grade grade) {
        return modelMapper.map(grade, GradeDto.class);
    }

    private Grade mapGradeDtoToGrade(GradeDto gradeDto) {
        return modelMapper.map(gradeDto, Grade.class);
    }
}
