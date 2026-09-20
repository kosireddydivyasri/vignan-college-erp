package com.example.stud_erp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDTO {
    private Long id;
    private String studentId;
    private String major;
    private int year;
    private Long studRollNo;
    private String studName;
    private String username;
    private String email;
    private String studPhoneNumber;
    private String studFatherName;
    private String studLastName;
    private int studentAge;
    private List<SemesterDTO> semesters;
    private List<AttendanceDTO> attendance;

    public StudentDTO(Long id, String studName) {
        this.id = id;
        this.studName = studName;
    }

    public StudentDTO(Long id, String studentId, String username, String email,
                      String major, int year, Long studRollNo, String studName,
                      String studFatherName, String studLastName, String studPhoneNumber) {
        this.id = id;
        this.studentId = studentId;
        this.username = username;
        this.email = email;
        this.major = major;
        this.year = year;
        this.studRollNo = studRollNo;
        this.studName = studName;
        this.studFatherName = studFatherName;
        this.studLastName = studLastName;
        this.studPhoneNumber = studPhoneNumber;
    }
}
