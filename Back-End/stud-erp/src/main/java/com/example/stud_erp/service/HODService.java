package com.example.stud_erp.service;

import com.example.stud_erp.entity.HOD;
import com.example.stud_erp.exception.OTPExpiredException;
import com.example.stud_erp.exception.ResourceNotFoundException;
import com.example.stud_erp.payload.LoginRequest;
import com.example.stud_erp.payload.ResetPasswordRequest;
import com.example.stud_erp.repository.HODRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class HODService {
    @Autowired private HODRepository hodRepository;
    @Autowired private EmailService emailService;

    public HOD saveHOD(HOD hod) { return hodRepository.save(hod); }
    public List<HOD> getAllHODs() { return hodRepository.findAll(); }
    public HOD getHODById(Long id) { return hodRepository.findById(id).orElse(null); }
    public void deleteHOD(Long id) { hodRepository.deleteById(id); }

    public HOD updateHOD(Long id, HOD details) {
        HOD hod = hodRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("HOD not found for id " + id));
        if (details.getName() != null && !details.getName().isBlank()) hod.setName(details.getName().trim());
        if (details.getDepartment() != null && !details.getDepartment().isBlank()) hod.setDepartment(details.getDepartment().trim());
        if (details.getUsername() != null && !details.getUsername().isBlank()) hod.setUsername(details.getUsername().trim());
        if (details.getEmail() != null && !details.getEmail().isBlank()) hod.setEmail(details.getEmail().trim());
        if (details.getPhone() != null && !details.getPhone().isBlank()) hod.setPhone(details.getPhone().trim());
        if (details.getSubjects() != null) hod.setSubjects(details.getSubjects());
        if (details.getPassword() != null && !details.getPassword().isBlank()) hod.setPassword(details.getPassword());
        hod.setUpdatedAt(LocalDateTime.now());
        return hodRepository.save(hod);
    }

    public HOD authenticateUser(LoginRequest request) {
        HOD user = hodRepository.findByUsername(request.getUsername());
        if (user == null || !request.getPassword().equals(user.getPassword())) throw new RuntimeException("Invalid username or password");
        return user;
    }

    public void sendForgotPasswordEmail(String email) {
        HOD user = hodRepository.findByEmail(email);
        if (user == null) throw new OTPExpiredException("User with email " + email + " not found");
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        user.setOtp(otp);
        hodRepository.save(user);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void verifyOTP(String email, String otp) {
        HOD user = hodRepository.findByEmail(email);
        if (user == null || user.getOtp() == null || !user.getOtp().equals(otp)) throw new OTPExpiredException("Invalid OTP");
    }

    public void resetPassword(ResetPasswordRequest request) {
        HOD hod = hodRepository.findByEmail(request.getEmail());
        if (hod == null) throw new OTPExpiredException("User with email " + request.getEmail() + " not found");
        hod.setPassword(request.getNewPassword());
        hod.setOtp(null);
        hodRepository.save(hod);
    }
}
