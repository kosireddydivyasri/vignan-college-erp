package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Semester;
import com.example.stud_erp.payload.SemesterDTO;
import com.example.stud_erp.repository.SemesterRepository;
import com.example.stud_erp.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {
    @Autowired private SemesterRepository semesterRepository;
    @Autowired private SemesterService semesterService;

    @PostMapping("/add")
    public ResponseEntity<?> createSemester(@RequestBody SemesterDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(semesterService.convertToDTO(semesterService.createSemester(dto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SemesterDTO>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.findAllDTO());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SemesterDTO>> getStudentSemesters(@PathVariable String studentId) {
        return ResponseEntity.ok(semesterService.findByStudentStudentId(studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemesterDTO> getSemester(@PathVariable Long id) {
        Semester semester = semesterRepository.findById(id).orElse(null);
        return semester == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(semesterService.convertToDTO(semester));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        if (!semesterRepository.existsById(id)) return ResponseEntity.notFound().build();
        semesterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
