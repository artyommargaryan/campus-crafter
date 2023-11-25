package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.AssignmentDto;
import com.inter.campuscrafter.entities.Assignment;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.exceptions.CourseNotFoundException;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Assignment Management", description = "Assignment Controller")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping("/courses/{courseId}/assignments")
    @Operation(description = "Fetches all assignments based on the provided course ID. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of assignments"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access forbidden"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentDto>> getAllAssignmentsByCourseId(@PathVariable String courseId,
                                                                           Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        if (!principal.hasRole(UserRole.STUDENT) && !userService.enrolledInCourse(principal.getId(), courseId)) {
            throw new AccessDeniedException("Not authorized to get this course.");
        }
        List<Assignment> allAssignmentsByCourseId = assignmentService.getAllAssignmentsByCourseId(courseId);
        List<AssignmentDto> allAssignmentDtosByCourseId = allAssignmentsByCourseId.stream().map(this::mapAssignmentToAssignmentDto).toList();
        return ResponseEntity.ok(allAssignmentDtosByCourseId);
    }

    @PostMapping("/courses/{courseId}/assignments")
    @Operation(description = "Creates a new assignment for the specified course. Accessible by teachers and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assignment created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> getAllAssignmentsByCourseId(@PathVariable String courseId,
                                                                     @RequestBody @Valid AssignmentDto assignmentDto,
                                                                     Authentication authentication) {
        if (!courseService.courseExists(courseId)) {
            throw new CourseNotFoundException("Course " + courseId + " not found");
        }
        User principal = (User) authentication.getPrincipal();
        courseService.isAuthorized(courseId, principal, "Not authorized to add assignment to this course.");
        Assignment assignment = mapAssignmentDtoTOAssignment(assignmentDto);
        Assignment createdAssignment = assignmentService.createAssignmentsByCourseId(courseId, assignment);
        AssignmentDto createdAssignmentDto = mapAssignmentToAssignmentDto(createdAssignment);
        return new ResponseEntity<>(createdAssignmentDto, HttpStatus.CREATED);
    }

    @PutMapping("/assignments/{id}")
    @Operation(description = "Updates the assignment specified by ID. Accessible by the teacher who created the assignment and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> updateAssignmentById(@PathVariable String id,
                                                              @RequestBody @Valid AssignmentDto assignmentDto,
                                                              Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        courseService.isAuthorized(assignmentDto.getCourseId(), principal, "Not authorized to update assignment for this course.");
        Assignment assignment = mapAssignmentDtoTOAssignment(assignmentDto);
        Assignment updatedAssignmentById = assignmentService.updateAssignmentById(id, assignment);
        if (updatedAssignmentById == null) {
            return ResponseEntity.notFound().build();
        }

        AssignmentDto updatedAssignmentDto = mapAssignmentToAssignmentDto(updatedAssignmentById);

        return ResponseEntity.ok(updatedAssignmentDto);
    }

    @DeleteMapping("/assignments/{id}")
    @Operation(description = "Deletes the assignment specified by ID. Accessible by the teacher who created the assignment and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssignmentById(@PathVariable String id) {
        assignmentService.deleteAssignmentById(id);
        return ResponseEntity.ok().build();
    }

    private Assignment mapAssignmentDtoTOAssignment(AssignmentDto assignmentDto) {
        return modelMapper.map(assignmentDto, Assignment.class);
    }

    private AssignmentDto mapAssignmentToAssignmentDto(Assignment assignment) {
        return modelMapper.map(assignment, AssignmentDto.class);
    }
}
