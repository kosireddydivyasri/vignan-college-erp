package com.example.stud_erp.repository;

import com.example.stud_erp.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByStudentStudentIdOrderBySemesterAsc(String studentId);
}
