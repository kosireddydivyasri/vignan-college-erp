package com.example.stud_erp.controller;

import com.example.stud_erp.entity.HOD;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.payload.ForgotPasswordRequest;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.service.HODService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hods")
public class HODController {
    @Autowired private HODService hodService;

    @PostMapping("/add-hod")
    public ResponseEntity<String> createHOD(@RequestBody HOD hod) {
        try {
            if (hod.getName() == null || hod.getDepartment() == null || hod.getUsername() == null || hod.getPassword() == null || hod.getEmail() == null || hod.getPhone() == null) {
                return ResponseEntity.badRequest().body("Name, department, username, password, email and phone are required.");
            }
            hodService.saveHOD(hod);
            return ResponseEntity.status(HttpStatus.CREATED).body("HOD registered successfully.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An HOD with the same username or email already exists.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHOD(@PathVariable Long id, @RequestBody HOD details) {
        try { return ResponseEntity.ok(hodService.updateHOD(id, details)); }
        catch (DataIntegrityViolationException e) { return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email is already in use."); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); }
    }

    @GetMapping("/get-hod")
    public ResponseEntity<List<HOD>> getAllHODs() { return ResponseEntity.ok(hodService.getAllHODs()); }

    @GetMapping("/{id}")
    public ResponseEntity<HOD> getHODById(@PathVariable Long id) {
        HOD hod = hodService.getHODById(id);
        return hod == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(hod);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHOD(@PathVariable Long id) { hodService.deleteHOD(id); return ResponseEntity.noContent().build(); }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
        try { return ResponseEntity.ok(hodService.authenticateUser(request)); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + ex.getMessage()); }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try { hodService.sendForgotPasswordEmail(request.getEmail()); return ResponseEntity.ok("OTP sent to your email successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try { hodService.verifyOTP(email, otp); return ResponseEntity.ok("OTP verified successfully"); }
        catch (OTPExpiredException ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try { hodService.resetPassword(request); return ResponseEntity.ok("Password reset successfully"); }
        catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()); }
    }
}
