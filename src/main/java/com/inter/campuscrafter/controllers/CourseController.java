package com.inter.campuscrafter.controllers;

import com.inter.campuscrafter.dto.CourseDto;
import com.inter.campuscrafter.entities.Course;
import com.inter.campuscrafter.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(@RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String teacherId) {
        List<CourseDto> courses = courseService.getAllCourses(Optional.ofNullable(status),
                Optional.ofNullable(teacherId));
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id) {
        CourseDto courseById = courseService.getCourseById(id);

        if (courseById == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(courseById);
    }

    @PostMapping
    public ResponseEntity<CourseDto> createCourse(@RequestBody Course course) {
        CourseDto createdCourse = courseService.createCourse(course);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<CourseDto> getCourseById(@PathVariable String id, @RequestBody Course course, @Requ) {
//        CourseDto courseById = courseService.updateCourseById(id);
//
//        if (courseById == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(courseById);
//    }

//    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
//        courseService.deleteCourse(id);
//        return ResponseEntity.ok().build();
//    }

}
