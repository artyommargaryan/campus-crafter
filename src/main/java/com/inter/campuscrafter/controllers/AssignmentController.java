package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.AssignmentDto;
import com.inter.campuscrafter.entities.Assignment;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.entities.UserRole;
import com.inter.campuscrafter.exceptions.CourseNotFoundException;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(tags = "Assignment Management", value = "Assignment Controller")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @GetMapping("/courses/{courseId}/assignments")
    @ApiOperation(value = "Get all assignments for a specific course", notes = "Fetches all assignments based on the provided course ID. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the list of assignments"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "Access forbidden"),
            @ApiResponse(code = 404, message = "Course not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentDto>> getAllAssignmentsByCourseId(@ApiParam(value = "Unique ID of the course", required = true)
                                                                           @PathVariable String courseId,
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
    @ApiOperation(value = "Create an assignment for a course", notes = "Creates a new assignment for the specified course. Accessible by teachers and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Assignment created successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "Forbidden access"),
            @ApiResponse(code = 404, message = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> getAllAssignmentsByCourseId(@ApiParam(value = "Unique ID of the course", required = true)
                                                                     @PathVariable String courseId,
                                                                     @ApiParam(value = "Assignment data to create", required = true)
                                                                     @RequestBody AssignmentDto assignmentDto,
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
    @ApiOperation(value = "Update an assignment", notes = "Updates the assignment specified by ID. Accessible by the teacher who created the assignment and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment updated successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "Forbidden access"),
            @ApiResponse(code = 404, message = "Assignment not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<AssignmentDto> updateAssignmentById(@ApiParam(value = "Unique ID of the assignment", required = true)
                                                              @PathVariable String id,
                                                              @ApiParam(value = "Updated assignment data", required = true)
                                                              @RequestBody AssignmentDto assignmentDto,
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
    @ApiOperation(value = "Delete an assignment", notes = "Deletes the assignment specified by ID. Accessible by the teacher who created the assignment and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Assignment deleted successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access"),
            @ApiResponse(code = 403, message = "Forbidden access"),
            @ApiResponse(code = 404, message = "Assignment not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAssignmentById(@ApiParam(value = "Unique ID of the assignment", required = true)
                                                     @PathVariable String id) {
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
