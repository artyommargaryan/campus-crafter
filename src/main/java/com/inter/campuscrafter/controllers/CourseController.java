package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dtos.CourseDto;
import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.User;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
import io.swagger.annotations.*;
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
@Api(tags = "Course Management", value = "Course Controller")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(value = "Get all courses", notes = "Retrieves a list of all courses with optional filters for status and teacher ID. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the list of courses"),
            @ApiResponse(code = 401, message = "Unauthorized access")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<CourseDto>> getAllCourses(@ApiParam(value = "Filter by course status")
                                                         @RequestParam(required = false) String status,
                                                         @ApiParam(value = "Filter by teacher ID")
                                                         @RequestParam(required = false) String teacherId) {
        List<Course> courses = courseService.getAllCourses(Optional.ofNullable(status),
                Optional.ofNullable(teacherId));
        List<CourseDto> courseDtos = courses.stream().map(this::mapCourseToCourseDto).toList();
        return ResponseEntity.ok(courseDtos);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a specific course", notes = "Retrieves detailed information about a specific course. Accessible by students, teachers, and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved course details"),
            @ApiResponse(code = 404, message = "Course not found")
    })
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<CourseDto> getCourseById(@ApiParam(value = "Unique ID of the course", required = true)
                                                   @PathVariable String id) {
        Course courseById = courseService.getCourseById(id);

        if (courseById == null) {
            return ResponseEntity.notFound().build();
        }

        CourseDto courseDto = mapCourseToCourseDto(courseById);

        return ResponseEntity.ok(courseDto);
    }

    @PostMapping
    @ApiOperation(value = "Create a course", notes = "Creates a new course. Accessible by teachers and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Course created successfully"),
            @ApiResponse(code = 401, message = "Unauthorized access")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(@ApiParam(value = "Course data to create", required = true)
                                                  @RequestBody CourseDto courseDto,
                                                  Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        Course course = mapCourseDtoToCourse(courseDto);
        Course createdCourse = courseService.createCourse(course, principal);
        CourseDto createdCourseDto = mapCourseToCourseDto(createdCourse);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update a course", notes = "Updates the course specified by ID. Accessible by the teacher who created the course and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Course updated successfully"),
            @ApiResponse(code = 404, message = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> getCourseById(@ApiParam(value = "Unique ID of the course", required = true)
                                                   @PathVariable String id,
                                                   @ApiParam(value = "Updated course data", required = true)
                                                   @RequestBody CourseDto updatedCourseDto,
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
    @ApiOperation(value = "Delete a course", notes = "Deletes the course specified by ID. Accessible by the teacher who created the course and admins.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Course deleted successfully"),
            @ApiResponse(code = 404, message = "Course not found")
    })
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@ApiParam(value = "Unique ID of the course to delete", required = true)
                                             @PathVariable String id,
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
