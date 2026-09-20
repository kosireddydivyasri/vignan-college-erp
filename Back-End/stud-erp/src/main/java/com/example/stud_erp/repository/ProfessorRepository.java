package com.example.stud_erp.repository;

import com.example.stud_erp.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Professor findByProfessorId(String professorId);

    Professor findByUsername(String username);

    Professor findByEmail(String email);
    boolean existsByProfessorIdOrUsernameOrEmail(String professorId, String username, String email);
    boolean existsByProfessorIdAndIdNot(String professorId, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
