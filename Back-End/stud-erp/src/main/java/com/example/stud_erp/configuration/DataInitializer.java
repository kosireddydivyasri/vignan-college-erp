package com.example.stud_erp.configuration;

import com.example.stud_erp.entity.Department;
import com.example.stud_erp.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedDepartments(DepartmentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<String> branches = List.of(
                        "Computer Science and Engineering",
                        "Information Technology",
                        "Electronics and Communication Engineering",
                        "Electrical and Electronics Engineering",
                        "Mechanical Engineering",
                        "Civil Engineering",
                        "Artificial Intelligence and Data Science"
                );
                branches.forEach(name -> repository.save(new Department(null, name, null)));
            }
        };
    }
}
