package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Semester;
import com.example.stud_erp.entity.Subject;
import com.example.stud_erp.payload.SubjectDTO;
import com.example.stud_erp.repository.SemesterRepository;
import com.example.stud_erp.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired private SubjectService subjectService;
    @Autowired private SemesterRepository semesterRepository;

    @PostMapping("/add")
    public ResponseEntity<?> createSubject(@RequestBody SubjectDTO dto) {
        if (dto.getSemester() == null || dto.getSemester().getId() == null) return ResponseEntity.badRequest().body("Semester is required.");
        Semester semester = semesterRepository.findById(dto.getSemester().getId()).orElse(null);
        if (semester == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Semester not found.");
        Subject subject = new Subject(); subject.setCode(dto.getCode()); subject.setName(dto.getName()); subject.setCredits(dto.getCredits()); subject.setGrade(dto.getGrade()); subject.setCt1(dto.getCt1()); subject.setCt2(dto.getCt2()); subject.setTheory(dto.getTheory()); subject.setSemester(semester);
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.addSubject(subject));
    }

    @GetMapping
    public List<Subject> getAllSubjects() { return subjectService.getAllSubjects(); }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) { return subjectService.getSubjectById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO dto) {
        Subject subject = subjectService.getSubjectById(id).orElse(null); if (subject == null) return ResponseEntity.notFound().build();
        subject.setCode(dto.getCode()); subject.setName(dto.getName()); subject.setCredits(dto.getCredits()); subject.setGrade(dto.getGrade()); subject.setCt1(dto.getCt1()); subject.setCt2(dto.getCt2()); subject.setTheory(dto.getTheory());
        return ResponseEntity.ok(subjectService.addSubject(subject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) { if (subjectService.getSubjectById(id).isEmpty()) return ResponseEntity.notFound().build(); subjectService.deleteSubject(id); return ResponseEntity.noContent().build(); }
}
