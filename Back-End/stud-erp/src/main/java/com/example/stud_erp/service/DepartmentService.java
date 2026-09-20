package com.example.stud_erp.service;

import com.example.stud_erp.entity.Department;
import com.example.stud_erp.exception.ResourceNotFoundException;
import com.example.stud_erp.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired private DepartmentRepository departmentRepository;

    public Department saveDepartment(Department department) { return departmentRepository.save(department); }
    public List<Department> getAllDepartments() { return departmentRepository.findAll(); }
    public Department getDepartmentById(Long id) { return departmentRepository.findById(id).orElse(null); }
    public void deleteDepartment(Long id) { departmentRepository.deleteById(id); }

    public Department updateDepartment(Long id, Department incoming) {
        Department department = departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found for id " + id));
        if (incoming.getName() != null && !incoming.getName().isBlank()) department.setName(incoming.getName().trim());
        if (incoming.getHod() != null) department.setHod(incoming.getHod());
        return departmentRepository.save(department);
    }
}
