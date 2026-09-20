package com.example.stud_erp.service;

import com.example.stud_erp.entity.Course;
import com.example.stud_erp.entity.Professor;
import com.example.stud_erp.repository.CourseRepository;
import com.example.stud_erp.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired private CourseRepository courseRepository;
    @Autowired private ProfessorRepository professorRepository;

    public Course addCourse(Course course) {
        if (course == null || course.getProfessor() == null || course.getProfessor().getId() == null) {
            throw new IllegalArgumentException("A professor must be assigned to the course.");
        }
        Professor professor = professorRepository.findById(course.getProfessor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Professor not found."));
        course.setProfessor(professor);
        addSubjectToProfessor(professor, course.getName());
        return courseRepository.save(course);
    }
    public List<Course> getAllCourses() { return courseRepository.findAll(); }
    public Optional<Course> getCourseById(Long id) { return courseRepository.findById(id); }
    public void deleteCourse(Long id) { courseRepository.deleteById(id); }
    public Course updateCourse(Long id, Course incoming) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found."));
        if (incoming.getCode() != null && !incoming.getCode().isBlank()) course.setCode(incoming.getCode().trim());
        if (incoming.getName() != null && !incoming.getName().isBlank()) course.setName(incoming.getName().trim());
        if (incoming.getCredits() > 0) course.setCredits(incoming.getCredits());
        if (incoming.getProfessor() != null && incoming.getProfessor().getId() != null) {
            Professor professor = professorRepository.findById(incoming.getProfessor().getId()).orElseThrow(() -> new RuntimeException("Professor not found."));
            course.setProfessor(professor);
            addSubjectToProfessor(professor, course.getName());
        } else {
            addSubjectToProfessor(course.getProfessor(), course.getName());
        }
        return courseRepository.save(course);
    }

    private void addSubjectToProfessor(Professor professor, String subject) {
        if (professor == null || subject == null || subject.isBlank()) return;
        if (professor.getSubjects() == null) professor.setSubjects(new java.util.ArrayList<>());
        if (!professor.getSubjects().contains(subject.trim())) professor.getSubjects().add(subject.trim());
        if (professor.getSubject() == null || professor.getSubject().isBlank()) professor.setSubject(subject.trim());
        professorRepository.save(professor);
    }
}
