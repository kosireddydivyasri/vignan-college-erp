package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Course;
import com.example.stud_erp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired private CourseService courseService;

    @PostMapping("/add")
    public ResponseEntity<?> addCourse(@RequestBody Course course) { try { return ResponseEntity.status(HttpStatus.CREATED).body(courseService.addCourse(course)); } catch (DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Course code already exists."); } }
    @GetMapping public ResponseEntity<List<Course>> getAllCourses() { return ResponseEntity.ok(courseService.getAllCourses()); }
    @GetMapping("/{id}") public ResponseEntity<Course> getCourseById(@PathVariable Long id) { return courseService.getCourseById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }
    @PutMapping("/{id}") public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course incoming) { try { return ResponseEntity.ok(courseService.updateCourse(id, incoming)); } catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); } }
    @DeleteMapping("/{id}") public ResponseEntity<Void> deleteCourse(@PathVariable Long id) { if (courseService.getCourseById(id).isEmpty()) return ResponseEntity.notFound().build(); courseService.deleteCourse(id); return ResponseEntity.noContent().build(); }
}
