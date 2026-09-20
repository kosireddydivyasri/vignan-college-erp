package com.example.stud_erp.service;

import com.example.stud_erp.entity.Professor;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.exception.ResourceNotFoundException;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.repository.ProfessorRepository;
import com.example.stud_erp.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Random;

@Service
public class ProfessorService {
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private EmailService emailService;

    public Professor saveProfessor(Professor professor) {
        if (professor == null) throw new IllegalArgumentException("Professor details are required.");
        if (blank(professor.getProfessorId())) throw new IllegalArgumentException("Professor ID is required.");
        if (blank(professor.getName())) throw new IllegalArgumentException("Professor name is required.");
        if (blank(professor.getDepartmentName())) throw new IllegalArgumentException("Department is required.");
        if (blank(professor.getSubject())) throw new IllegalArgumentException("Primary subject is required.");
        if (blank(professor.getUsername())) throw new IllegalArgumentException("Username is required.");
        if (blank(professor.getEmail())) throw new IllegalArgumentException("Email is required.");
        if (blank(professor.getPassword()) || professor.getPassword().length() < 6) throw new IllegalArgumentException("Password is required and must be at least 6 characters long.");
        if (professorRepository.existsByProfessorIdOrUsernameOrEmail(professor.getProfessorId(), professor.getUsername(), professor.getEmail())) {
            throw new DataIntegrityViolationException("Professor ID, username, or email is already in use.");
        }
        departmentRepository.findByName(professor.getDepartmentName().trim()).ifPresent(professor::setDepartment);
        if (professor.getSubjects() == null || professor.getSubjects().isEmpty()) {
            professor.setSubjects(new java.util.ArrayList<>(java.util.List.of(professor.getSubject().trim())));
        }
        return professorRepository.save(professor);
    }
    public List<Professor> getAllProfessors() { return professorRepository.findAll(); }
    public Professor getProfessorById(String id) { return professorRepository.findByProfessorId(id); }
    public Professor getProfessorByDatabaseId(Long id) { return professorRepository.findById(id).orElse(null); }
    public void deleteProfessor(Long id) { professorRepository.deleteById(id); }

    public Professor updateProfessor(Long id, Professor incoming) {
        Professor professor = professorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Professor not found for id " + id));
        if (incoming.getProfessorId() != null && !incoming.getProfessorId().isBlank()) {
            String value = incoming.getProfessorId().trim();
            if (professorRepository.existsByProfessorIdAndIdNot(value, id)) throw new DataIntegrityViolationException("Professor ID is already in use.");
            professor.setProfessorId(value);
        }
        if (incoming.getName() != null && !incoming.getName().isBlank()) professor.setName(incoming.getName().trim());
        if (incoming.getDepartmentName() != null && !incoming.getDepartmentName().isBlank()) {
            professor.setDepartmentName(incoming.getDepartmentName().trim());
            departmentRepository.findByName(professor.getDepartmentName()).ifPresent(professor::setDepartment);
        }
        if (incoming.getSubject() != null && !incoming.getSubject().isBlank()) professor.setSubject(incoming.getSubject().trim());
        if (incoming.getUsername() != null && !incoming.getUsername().isBlank()) {
            String value = incoming.getUsername().trim();
            if (professorRepository.existsByUsernameAndIdNot(value, id)) throw new DataIntegrityViolationException("Professor username is already in use.");
            professor.setUsername(value);
        }
        if (incoming.getEmail() != null && !incoming.getEmail().isBlank()) {
            String value = incoming.getEmail().trim();
            if (professorRepository.existsByEmailAndIdNot(value, id)) throw new DataIntegrityViolationException("Professor email is already in use.");
            professor.setEmail(value);
        }
        if (incoming.getSubjects() != null) professor.setSubjects(incoming.getSubjects());
        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) professor.setPassword(incoming.getPassword());
        return professorRepository.save(professor);
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }

    public Professor authenticateUser(LoginRequest request) {
        Professor user = professorRepository.findByUsername(request.getUsername());
        if (user == null || !request.getPassword().equals(user.getPassword())) throw new RuntimeException("Invalid username or password");
        return user;
    }

    public void sendForgotPasswordEmail(String email) {
        Professor user = professorRepository.findByEmail(email);
        if (user == null) throw new OTPExpiredException("User with email " + email + " not found");
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp); professorRepository.save(user); emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void verifyOTP(String email, String otp) {
        Professor user = professorRepository.findByEmail(email);
        if (user == null || user.getOtp() == null || !user.getOtp().equals(otp)) throw new OTPExpiredException("Invalid OTP");
    }

    public void resetPassword(ResetPasswordRequest request) {
        Professor user = professorRepository.findByEmail(request.getEmail());
        if (user == null) throw new OTPExpiredException("User with email " + request.getEmail() + " not found");
        user.setPassword(request.getNewPassword()); user.setOtp(null); professorRepository.save(user);
    }
}
