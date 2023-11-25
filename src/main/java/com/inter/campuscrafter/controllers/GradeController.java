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
import io.swagger.annotations.*;
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
@Api(tags = "Grade Management", value = "Grade Controller")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/assignments/{assignmentId}/grades")
    @ApiOperation(value = "Submit a grade for an assignment", notes = "Allows teachers or admins to submit grades for a specific assignment.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Grade submitted successfully"),
            @ApiResponse(code = 404, message = "Assignment not found"),
            @ApiResponse(code = 403, message = "Forbidden - User not authorized to submit grade for this assignment")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<GradeDto> getGrade(@ApiParam(value = "ID of the assignment to grade", required = true)
                                             @PathVariable String assignmentId,
                                             @ApiParam(value = "Grade details", required = true)
                                             GradeDto gradeDto,
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
    @ApiOperation(value = "Get all grades for a student", notes = "Retrieves all grades for a specified student. Accessible by students for their own grades, teachers for students in their courses, and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Grades retrieved successfully"),
            @ApiResponse(code = 404, message = "Student not found"),
            @ApiResponse(code = 403, message = "Forbidden - User not authorized to view these grades")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<GradeDto>> getAllGradesByStudentId(@ApiParam(value = "ID of the student", required = true)
                                                                  @PathVariable String studentId,
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
