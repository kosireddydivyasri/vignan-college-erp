package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Student;
import com.example.stud_erp.exception.CustomException;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.payload.ForgotPasswordRequest;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.payload.StudentDTO;
import com.example.stud_erp.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired private StudentService studentService;

    @PostMapping("/add-student")
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            String validation = validateStudentForCreate(student);
            if (validation != null) return ResponseEntity.badRequest().body(validation);

            if (studentService.existsByUniqueFields(student.getStudentId(), student.getUsername(), student.getEmail(), student.getStudRollNo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Student ID, Roll Number, Username, or Email is already in use.");
            }
            studentService.addStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body("Student registered successfully.");
        } catch (DataIntegrityViolationException | CustomException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving student data: " + e.getMessage());
        }
    }

    private String validateStudentForCreate(Student student) {
        if (student == null) return "Student details are required.";
        if (blank(student.getStudentId())) return "Student ID is required.";
        if (blank(student.getStudName())) return "First name is required.";
        if (blank(student.getStudLastName())) return "Last name is required.";
        if (blank(student.getStudFatherName())) return "Father's name is required.";
        if (student.getStudRollNo() == null) return "Roll Number is required.";
        if (blank(student.getMajor())) return "Branch / Major is required.";
        if (student.getYear() < 1 || student.getYear() > 4) return "B.Tech year must be between 1 and 4.";
        if (student.getStudentAge() < 15 || student.getStudentAge() > 100) return "Enter a valid age between 15 and 100.";
        if (blank(student.getStudPhoneNumber())) return "Phone number is required.";
        if (blank(student.getEmail())) return "Email is required.";
        if (blank(student.getUsername())) return "Username is required.";
        if (blank(student.getPassword()) || student.getPassword().length() < 6) return "Password is required and must be at least 6 characters long.";
        return null;
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }

    @GetMapping
    public List<StudentDTO> getAllStudents() { return studentService.findAll(); }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDTO> getStudentByStudentId(@PathVariable String studentId) {
        StudentDTO student = studentService.getStudentDTOByStudentId(studentId);
        return student == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        try { return ResponseEntity.ok(studentService.updateStudent(id, updatedStudent)); }
        catch (CustomException ex) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); }
        catch (DataIntegrityViolationException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Student username, email, ID or roll number is already in use."); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try { return ResponseEntity.ok(studentService.authenticateUser(request)); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + ex.getMessage()); }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try { studentService.sendForgotPasswordEmail(request.getEmail()); return ResponseEntity.ok("OTP sent to your email successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage()); }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try { studentService.verifyOTP(email, otp); return ResponseEntity.ok("OTP verified successfully"); }
        catch (OTPExpiredException ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try { studentService.resetPassword(request); return ResponseEntity.ok("Password reset successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }
}
