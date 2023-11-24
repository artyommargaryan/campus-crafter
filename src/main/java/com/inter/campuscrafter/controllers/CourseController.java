package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dto.CourseDto;
import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.entities.UserProfile;
import com.inter.campuscrafter.services.AssignmentService;
import com.inter.campuscrafter.services.CourseService;
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
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String teacherId) {
        List<Course> courses = courseService.getAllCourses(Optional.ofNullable(status),
                Optional.ofNullable(teacherId));
        List<CourseDto> courseDtos = courses.stream().map(this::mapCourseToCourseDto).toList();
        return ResponseEntity.ok(courseDtos);
    }

    @GetMapping("/{id}")
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
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto, Authentication authentication) {
        UserProfile principal = (UserProfile) authentication.getPrincipal();
        Course course = mapCourseDtoToCourse(courseDto);
        Course createdCourse = courseService.createCourse(course, principal);
        CourseDto createdCourseDto = mapCourseToCourseDto(createdCourse);
        return new ResponseEntity<>(createdCourseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id,
                                                   @RequestBody CourseDto updatedCourseDto,
                                                    Authentication authentication) {
        UserProfile principal = (UserProfile) authentication.getPrincipal();
        Course course = mapCourseDtoToCourse(updatedCourseDto);
        Course updatedCourse = courseService.updateCourseById(id, course, principal);

        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }

        CourseDto courseDto = mapCourseToCourseDto(updatedCourse);

        return ResponseEntity.ok(courseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id, Authentication authentication) {
        UserProfile principal = (UserProfile) authentication.getPrincipal();
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
