package com.example.stud_erp.service;

import com.example.stud_erp.entity.*;
import com.example.stud_erp.payload.*;
import com.example.stud_erp.repository.CourseRepository;
import com.example.stud_erp.repository.SemesterRepository;
import com.example.stud_erp.repository.StudentRepository;
import com.example.stud_erp.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterService {
    @Autowired private SemesterRepository semesterRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private PracticalService practicalService;

    @Transactional(readOnly = true)
    public SemesterDTO convertToDTO(Semester semester) {
        CourseDTO courseDTO = null;
        if (semester.getCourse() != null) {
            Professor professor = semester.getCourse().getProfessor();
            courseDTO = new CourseDTO(semester.getCourse().getId(), semester.getCourse().getCode(), semester.getCourse().getName(),
                    professor == null ? null : new ProfessorDTO(professor.getId(), professor.getName()));
        }
        StudentDTO studentDTO = semester.getStudent() == null ? null : new StudentDTO(semester.getStudent().getId(), semester.getStudent().getStudName());
        SemesterDTO dto = new SemesterDTO(semester.getId(), semester.getSemester(), courseDTO, studentDTO);
        dto.setSubjects(semester.getSubjects() == null ? Collections.emptyList() : semester.getSubjects().stream().map(this::subjectToDTO).collect(Collectors.toList()));
        dto.setPracticals(semester.getPracticals() == null ? Collections.emptyList() : semester.getPracticals().stream().map(this::practicalToDTO).collect(Collectors.toList()));
        return dto;
    }

    private SubjectDTO subjectToDTO(Subject subject) {
        return new SubjectDTO(subject.getId(), subject.getCode(), subject.getName(), subject.getCredits(), subject.getGrade(), subject.getCt1(), subject.getCt2(), subject.getTheory(), null);
    }

    private PracticalDTO practicalToDTO(Practical practical) {
        return new PracticalDTO(practical.getId(), practical.getName(), practical.getGrade(), practical.getWritten(), practical.getViva(), null);
    }

    @Transactional(readOnly = true)
    public List<SemesterDTO> findAllDTO() { return semesterRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList()); }

    @Transactional(readOnly = true)
    public List<SemesterDTO> findByStudentStudentId(String studentId) {
        return semesterRepository.findByStudentStudentIdOrderBySemesterAsc(studentId).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    public void createSubjects(List<SubjectDTO> subjectDTOs, Semester semester) {
        if (subjectDTOs == null) return;
        for (SubjectDTO dto : subjectDTOs) {
            Subject subject = new Subject(); subject.setCode(dto.getCode()); subject.setName(dto.getName()); subject.setCredits(dto.getCredits()); subject.setGrade(dto.getGrade()); subject.setCt1(dto.getCt1()); subject.setCt2(dto.getCt2()); subject.setTheory(dto.getTheory()); subject.setSemester(semester); subjectRepository.save(subject);
        }
    }

    public Semester createSemester(SemesterDTO dto) {
        if (dto.getCourse() == null || dto.getCourse().getId() == null) throw new RuntimeException("Course is required.");
        if (dto.getStudent() == null || dto.getStudent().getId() == null) throw new RuntimeException("Student is required.");
        Course course = courseRepository.findById(dto.getCourse().getId()).orElseThrow(() -> new RuntimeException("Course not found."));
        Student student = studentRepository.findById(dto.getStudent().getId()).orElseThrow(() -> new RuntimeException("Student not found."));
        Semester semester = new Semester(); semester.setSemester(dto.getSemester()); semester.setCourse(course); semester.setStudent(student); semester = semesterRepository.save(semester);
        createSubjects(dto.getSubjects(), semester);
        if (dto.getPracticals() != null) for (PracticalDTO p : dto.getPracticals()) {
            Practical practical = new Practical(); practical.setName(p.getName()); practical.setGrade(p.getGrade()); practical.setWritten(p.getWritten()); practical.setViva(p.getViva()); practical.setSemester(semester); practicalService.createPracticalEntity(practical);
        }
        return semester;
    }
}
