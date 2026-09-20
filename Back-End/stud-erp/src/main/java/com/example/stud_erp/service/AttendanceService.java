package com.example.stud_erp.service;


import com.example.stud_erp.entity.Attendance;
import com.example.stud_erp.entity.ClassSession;
import com.example.stud_erp.entity.Student;
import com.example.stud_erp.repository.AttendanceRepository;
import com.example.stud_erp.repository.ClassRepository;
import com.example.stud_erp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private ClassRepository classSessionRepository;

    @Autowired
    private AttendanceRepository attendanceRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    public ClassSession saveAttendance(String lecturer, String subject, LocalDate attendanceDate, LocalTime time, Map<String, String> students) {
        List<Attendance> existing = attendanceRecordRepository
                .findByClassSessionLecturerAndClassSessionSubjectAndAttendanceDate(lecturer, subject, attendanceDate);

        ClassSession classSession = null;
        if (!existing.isEmpty() && existing.get(0).getClassSession() != null
                && time.equals(existing.get(0).getClassSession().getTime())) {
            classSession = existing.get(0).getClassSession();
        }

        if (classSession == null) {
            classSession = new ClassSession();
            classSession.setLecturer(lecturer);
            classSession.setSubject(subject);
            classSession.setTime(time);
            classSession.setAttendance(new ArrayList<>());
        }

        Map<Long, Attendance> existingByStudent = classSession.getAttendance() == null
                ? new java.util.HashMap<>()
                : classSession.getAttendance().stream()
                    .filter(a -> a.getStudent() != null)
                    .collect(Collectors.toMap(a -> a.getStudent().getId(), a -> a, (a, b) -> a));

        for (Map.Entry<String, String> entry : students.entrySet()) {
            Student student = studentRepository.findByStudentId(entry.getKey());
            if (student == null) throw new IllegalArgumentException("Student not found with ID: " + entry.getKey());

            Attendance record = existingByStudent.get(student.getId());
            if (record == null) {
                record = new Attendance();
                record.setClassSession(classSession);
                record.setStudent(student);
                record.setStudentName(student.getStudName() + " " + student.getStudLastName());
                classSession.getAttendance().add(record);
            }
            record.setStatus(entry.getValue());
            record.setAttendanceDate(attendanceDate);
        }

        return classSessionRepository.save(classSession);
    }


    public Map<LocalDate, List<Attendance>> getAttendanceByLecturerAndSubject(String lecturer, String subject) {
        List<Attendance> records = attendanceRecordRepository.findByClassSessionLecturerAndClassSessionSubject(lecturer, subject);

        return records.stream().collect(Collectors.groupingBy(Attendance::getAttendanceDate));
    }

}
