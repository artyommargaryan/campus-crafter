package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.CourseDto;
import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Management", description = "Course Controller")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final ModelMapper modelMapper;

    @GetMapping
    @Operation(description = "Retrieves a list of all courses with optional filters for status and teacher ID. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of courses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String teacherId) {
        List<Course> courses = courseService.getAllCourses(Optional.ofNullable(status),
                Optional.ofNullable(teacherId));
        List<CourseDto> courseDtos = courses.stream().map(this::mapCourseToCourseDto).toList();
        return ResponseEntity.ok(courseDtos);
    }

    @GetMapping("/{id}")
    @Operation(description = "Retrieves detailed information about a specific course. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved course details"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id) {
        Course courseById = courseService.getCourseById(id);

        if (courseById == null) {
            return ResponseEntity.notFound().build();
        }

        CourseDto courseDto = mapCourseToCourseDto(courseById);

        return ResponseEntity.ok(courseDto);
    }

    @PostMapping
    @Operation(description = "Creates a new course. Accessible by teachers and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(@RequestBody @Valid CourseDto courseDto,
                                                  Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        Course course = mapCourseDtoToCourse(courseDto);
        Course createdCourse = courseService.createCourse(course, principal);
        CourseDto createdCourseDto = mapCourseToCourseDto(createdCourse);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(description = "Updates the course specified by ID. Accessible by the teacher who created the course and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id,
                                                   @RequestBody @Valid CourseDto updatedCourseDto,
                                                   Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        Course course = mapCourseDtoToCourse(updatedCourseDto);
        Course updatedCourse = courseService.updateCourseById(id, course, principal);

        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }

        CourseDto courseDto = mapCourseToCourseDto(updatedCourse);

        return ResponseEntity.ok(courseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Deletes the course specified by ID. Accessible by the teacher who created the course and admins.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id,
                                             Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        courseService.deleteCourse(id, principal);
        assignmentService.deleteAllByCourseId(id);

        return ResponseEntity.noContent().build();
    }

    private Course mapCourseDtoToCourse(CourseDto courseDto) {
        return modelMapper.map(courseDto, Course.class);
    }

    private CourseDto mapCourseToCourseDto(Course course) {
        return modelMapper.map(course, CourseDto.class);
    }


}
