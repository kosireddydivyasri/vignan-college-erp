package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Department;
import com.example.stud_erp.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired private DepartmentService departmentService;

    @PostMapping("/add-dept")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.saveDepartment(department)); }
        catch (DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists."); }
    }

    @GetMapping("/get-dept")
    public ResponseEntity<List<Department>> getAllDepartments() { return ResponseEntity.ok(departmentService.getAllDepartments()); }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return department == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(department);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try { return ResponseEntity.ok(departmentService.updateDepartment(id, department)); }
        catch (DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists."); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (departmentService.getDepartmentById(id) == null) return ResponseEntity.notFound().build();
        departmentService.deleteDepartment(id); return ResponseEntity.noContent().build();
    }
}
