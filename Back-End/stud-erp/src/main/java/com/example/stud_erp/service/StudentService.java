package com.example.stud_erp.service;

import com.example.stud_erp.entity.Student;
import com.example.stud_erp.exception.CustomException;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.payload.StudentDTO;
import com.example.stud_erp.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentService {
    @Autowired private StudentRepository studentRepository;
    @Autowired private EmailService emailService;

    @Cacheable("students")
    public List<StudentDTO> findAll() {
        return studentRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public StudentDTO getStudentDTOByStudentId(String studentId) {
        Student student = studentRepository.findByStudentId(studentId);
        return student == null ? null : convertToDTO(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public boolean existsByUniqueFields(String studentId, String username, String email, Long rollNo) {
        return studentRepository.existsByStudentIdOrUsernameOrEmailOrStudRollNo(studentId, username, email, rollNo);
    }

    public Student addStudent(Student student) {
        try {
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException("Student with the same ID, Roll Number, Username, or Email already exists.");
        }
    }

    public Student updateStudent(Long id, Student updated) {
        return studentRepository.findById(id).map(student -> {
            student.setStudentId(required(updated.getStudentId(), student.getStudentId()));
            student.setUsername(required(updated.getUsername(), student.getUsername()));
            student.setEmail(required(updated.getEmail(), student.getEmail()));
            student.setStudName(required(updated.getStudName(), student.getStudName()));
            student.setStudFatherName(required(updated.getStudFatherName(), student.getStudFatherName()));
            student.setStudLastName(required(updated.getStudLastName(), student.getStudLastName()));
            student.setStudPhoneNumber(required(updated.getStudPhoneNumber(), student.getStudPhoneNumber()));
            student.setMajor(required(updated.getMajor(), student.getMajor()));
            if (updated.getYear() > 0) student.setYear(updated.getYear());
            if (updated.getStudRollNo() != null) student.setStudRollNo(updated.getStudRollNo());
            if (updated.getStudentAge() > 0) student.setStudentAge(updated.getStudentAge());
            if (updated.getPassword() != null && !updated.getPassword().isBlank()) student.setPassword(updated.getPassword());
            return studentRepository.save(student);
        }).orElseThrow(() -> new CustomException("Student not found with id " + id));
    }

    private String required(String incoming, String current) {
        return incoming == null || incoming.isBlank() ? current : incoming.trim();
    }

    public void deleteStudent(Long id) { studentRepository.deleteById(id); }

    public Student authenticateUser(LoginRequest request) {
        Student user = studentRepository.findByUsername(request.getUsername());
        if (user == null || !request.getPassword().equals(user.getPassword())) throw new RuntimeException("Invalid username or password");
        return user;
    }

    public void sendForgotPasswordEmail(String email) {
        Student user = studentRepository.findByEmail(email);
        if (user == null) throw new OTPExpiredException("User with email " + email + " not found");
        String otp = generateOTP();
        user.setOtp(otp);
        studentRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    private String generateOTP() { return String.valueOf(100000 + new Random().nextInt(900000)); }

    public void verifyOTP(String email, String otp) {
        Student user = studentRepository.findByEmail(email);
        if (user == null || user.getOtp() == null || !user.getOtp().equals(otp)) throw new OTPExpiredException("Invalid OTP");
    }

    public void resetPassword(ResetPasswordRequest request) {
        Student user = studentRepository.findByEmail(request.getEmail());
        if (user == null) throw new OTPExpiredException("User with email " + request.getEmail() + " not found");
        user.setPassword(request.getNewPassword());
        user.setOtp(null);
        studentRepository.save(user);
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setStudentId(student.getStudentId());
        dto.setUsername(student.getUsername());
        dto.setEmail(student.getEmail());
        dto.setMajor(student.getMajor());
        dto.setYear(student.getYear());
        dto.setStudRollNo(student.getStudRollNo());
        dto.setStudName(student.getStudName());
        dto.setStudFatherName(student.getStudFatherName());
        dto.setStudLastName(student.getStudLastName());
        dto.setStudPhoneNumber(student.getStudPhoneNumber());
        dto.setStudentAge(student.getStudentAge());
        return dto;
    }
}
