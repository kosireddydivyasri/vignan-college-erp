package com.example.stud_erp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SemesterDTO {
    private Long id;
    private String semester;
    private CourseDTO course;
    private StudentDTO student;
    private List<PracticalDTO> practicals = new ArrayList<>();
    private List<SubjectDTO> subjects = new ArrayList<>();

    public SemesterDTO(Long id, String semester, CourseDTO courseDTO) {
        this.id = id;
        this.semester = semester;
        this.course = courseDTO;
    }

    public SemesterDTO(Long id, String semester, CourseDTO courseDTO, StudentDTO studentDTO) {
        this.id = id;
        this.semester = semester;
        this.course = courseDTO;
        this.student = studentDTO;
    }
}
