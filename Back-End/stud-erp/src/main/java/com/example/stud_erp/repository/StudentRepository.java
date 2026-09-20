package com.example.stud_erp.repository;

import com.example.stud_erp.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUsernameAndPassword(String username, String password);
    Student findByUsername(String username);
    Student findByEmail(String email);
    boolean existsByStudentId(String studentId);
    boolean existsByStudRollNo(Long rollNo);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Student findByStudName(String studName);
    Student findByStudentId(String id);
    boolean existsByStudentIdOrUsernameOrEmailOrStudRollNo(String studentId, String username, String email, Long rollNo);
}
