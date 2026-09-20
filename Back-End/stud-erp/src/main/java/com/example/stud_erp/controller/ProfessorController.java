package com.example.stud_erp.controller;

import com.example.stud_erp.entity.Professor;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.payload.ForgotPasswordRequest;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    @Autowired private ProfessorService professorService;

    @PostMapping("/add-prof")
    public ResponseEntity<?> createProfessor(@RequestBody Professor professor) {
        try {
            professorService.saveProfessor(professor);
            return ResponseEntity.status(HttpStatus.CREATED).body("Professor registered successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving professor data: " + e.getMessage());
        }
    }

    @GetMapping({"/get-prof", ""})
    public ResponseEntity<List<Professor>> getAllProfessors() { return ResponseEntity.ok(professorService.getAllProfessors()); }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> getProfessorById(@PathVariable String id) {
        Professor professor = professorService.getProfessorById(id);
        return professor == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(professor);
    }

    @PutMapping("/by-id/{id}")
    public ResponseEntity<?> updateProfessor(@PathVariable Long id, @RequestBody Professor professor) {
        try { return ResponseEntity.ok(professorService.updateProfessor(id, professor)); }
        catch (DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Professor ID, username or email is already in use."); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable Long id) { professorService.deleteProfessor(id); return ResponseEntity.noContent().build(); }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try { return ResponseEntity.ok(professorService.authenticateUser(request)); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + ex.getMessage()); }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try { professorService.sendForgotPasswordEmail(request.getEmail()); return ResponseEntity.ok("OTP sent to your email successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try { professorService.verifyOTP(email, otp); return ResponseEntity.ok("OTP verified successfully"); }
        catch (OTPExpiredException ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try { professorService.resetPassword(request); return ResponseEntity.ok("Password reset successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }
}
