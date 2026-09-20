package com.example.stud_erp.repository;

import com.example.stud_erp.entity.HOD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HODRepository extends JpaRepository<HOD, Long> {
    HOD findByUsername(String username);
    HOD findByEmail(String email);
}
