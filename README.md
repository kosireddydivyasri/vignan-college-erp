# Vignan College ERP System

## 📚 Description

The **Vignan College ERP System** is a role-based academic management application developed as a B.Tech final-year mini project. It brings common academic and administrative workflows into one web application for **HODs, Professors, and Students**.

The project uses a React frontend, a Java Spring Boot REST backend, and MySQL for persistent data. 

## 🌟 Features

### Frontend

- **React** with **Vite** for the user interface.
- **React Router** for role-based navigation.
- **Redux Toolkit** for application state where required by the existing dashboard structure.
- **Material Tailwind** and TailwindCSS for responsive UI components.
- **Axios** for REST API communication.
- **ApexCharts** support for dashboard visualisation.
- Separate sign-in/sign-up flows for HOD, Professor, and Student.

### Backend

- **Java Spring Boot** REST APIs.
- **Spring Data JPA / Hibernate** for database persistence.
- Role-oriented workflows for HOD, Professor, and Student.
- CRUD operations for students, professors, departments, courses, semesters, and subjects.
- Student-specific semester and subject records.
- Department-aware professor and student management.
- Attendance recording and attendance history.
- Notification APIs for students and professors.
- Email/OTP support for the existing password-recovery workflow.
- MySQL database integration.

## 👥 Roles and Functionalities

### 1. HOD

The HOD is responsible for academic and administrative management.

- Register and sign in as HOD.
- View and update the HOD's own information.
- Manage Students:
  - Add students.
  - Edit students.
  - Delete students.
  - View student details.
  - Assign branch/major and B.Tech year.
- Manage Professors:
  - Add professors.
  - Edit professors.
  - Delete professors.
  - Assign professors to departments.
  - Assign subjects/courses.
- Manage Departments/Branches:
  - Add departments.
  - Edit departments.
  - Delete departments where allowed by existing relationships.
- Manage Courses:
  - Create courses/subjects.
  - Assign a professor to a course.
- Manage Semester Records:
  - Create a semester record for a specific student.
  - Associate the semester with a course.
  - View semester records for all students or a selected student.
- Manage Subjects:
  - Add subjects to a student's semester record.
  - Update subject marks/credits/grade.
  - Delete subjects.
- View student academic/semester details from the HOD academic records page.
- Manage notifications and email functionality provided by the existing application.

### 2. Professor

The Professor manages teaching and attendance activities assigned to the professor.

- Register and sign in as Professor.
- View and update personal information.
- View assigned subjects.
- Take attendance for students in the professor's department.
- Select a real subject, date, and time for an attendance session.
- Mark students Present/Absent.
- Save attendance to MySQL.
- View attendance history for the currently signed-in professor and selected subject.
- View student records available through the existing professor dashboard.
- Send/view notifications using the existing notification functionality.

### 3. Student

The Student can access personal and academic information.

- Register and sign in as Student.
- View the student's own information.
- Update personal information.
- Update branch/major, year, contact details, and password through the profile page.
- View only the semester records assigned to the logged-in student.
- View subjects, credits, internal marks, theory marks, total marks, and grades maintained by the HOD.
- View practical records when they have been added to the student's semester.
- View notifications and attendance information supported by the application.

## 🎓 Academic Data Flow

The academic records are designed to follow this flow:

```text
HOD
 │
 ├── Creates Department / Branch
 │
 ├── Creates Professor
 │
 ├── Creates Course and assigns Professor
 │
 ├── Creates Semester Record for a Student
 │
 └── Adds Subjects / Marks to that Semester
             │
             ▼
        Student Dashboard
             │
             └── Student sees only their own semester records

Professor
 │
 ├── Signs in
 ├── Sees assigned subjects
 ├── Selects subject + date + time
 ├── Sees students from the professor's department
 └── Saves attendance
             │
             ▼
          MySQL
```

There are **no default demo subjects or demo marks**. Academic records appear only after they are created through the HOD academic-management workflow.

There are **no default lecturer names** in the attendance screens. Attendance history is restricted to the currently authenticated professor and the subjects associated with that professor.

## 🛠️ Tech Stack

### Frontend

- React 18
- Vite
- React Router DOM
- Redux Toolkit / React Redux
- Axios
- TailwindCSS
- Material Tailwind React
- Heroicons
- ApexCharts / React ApexCharts

### Backend

- Java 17
- Spring Boot 3.2.x
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security components used by the application
- Maven
- MySQL

### Storage

- MySQL


## 🚀 Project Structure

```text
Vignan-College-ERP/
├── Back-End/
│   └── stud-erp/
│       ├── src/main/java/
│       ├── src/main/resources/
│       ├── pom.xml
│       └── mvnw.cmd
│
├── Fron-End/
│   └── College-ERP/
│       ├── src/
│       ├── package.json
│       ├── vite.config.js
│       └── index.html
│
└── README.md
```

## 📋 Setup Instructions

### Prerequisites

Install the following on the development machine:

- Node.js and npm
- Java JDK 17 or later
- MySQL Server
- MySQL Workbench (recommended)
- Git (for repository management)

No Firebase account or Firebase configuration is required for this version.

### 1. Database Setup

Start MySQL and create the database:

```sql
CREATE DATABASE IF NOT EXISTS college;
```

Open:

```text
Back-End/stud-erp/src/main/resources/application.properties
```

Configure the MySQL connection for the local machine. The default development configuration is intended for:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/college
spring.datasource.username=root
spring.datasource.password=root
```

Change the username/password if the local MySQL installation uses different credentials.

The application uses JPA/Hibernate to create/update the required tables when the backend starts.

### 2. Backend Setup

From the project root:

```bat
cd Back-End\stud-erp
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

The root URL is an API server and may return a Spring Boot 404 page. That is normal; the React application is the user-facing interface.

### 3. Frontend Setup

Open a second terminal from the project root:

```bat
cd Fron-End\College-ERP
npm install
npm run dev
```

Open the URL printed by Vite, normally:

```text
http://localhost:5173/
```

### 4. Recommended First-Time Test Order

1. Start MySQL.
2. Start the Spring Boot backend.
3. Start the React frontend.
4. Register an HOD.
5. Sign in as HOD.
6. Confirm departments/branches are available.
7. Add a Professor and assign the department.
8. Add a Student and assign the matching branch.
9. Create a Course and assign it to the Professor.
10. Create a Semester Record for the Student.
11. Add Subjects to that semester.
12. Sign in as the Student and verify the semester/subject records.
13. Sign in as the Professor and verify the assigned subject.
14. Take attendance for the department students.
15. Open attendance history and verify that only the signed-in professor's records are shown.

## 🔐 Account and Data Notes

- Student, Professor, and HOD usernames/emails are unique according to the database constraints.
- New Student and Professor accounts require a password of at least 6 characters.
- When editing an account, leaving the password field empty keeps the existing password.
- Student B.Tech year is represented as 1–4.
- Branch/Major is selected from the configured department/branch list.
- Academic records are stored against the relevant student, semester, course, and subject entities.

## 📧 Notifications and Email

The original project contains notification and email functionality. These modules remain in the adapted version where they are supported by the current backend/frontend flow.

Email features that require SMTP credentials must be configured in the backend `application.properties` before they can send real emails.

## 🧪 Development Notes

The project is intended as an academic demonstration of a College ERP architecture. For production deployment, additional security hardening would be appropriate, including password hashing, stronger authentication/session management, authorization at every API endpoint, validation, audit logging, secret management, and HTTPS.


## 💡 Contribution

This repository is maintained as a B.Tech academic project. Improvements to the academic workflow, validation, UI, and documentation can be added through normal Git version-control practices.

## 📄 Academic Purpose

This project is developed for a **B.Tech final-year academic submission** to demonstrate the design and implementation of a role-based College ERP using React, Spring Boot, REST APIs, JPA/Hibernate, and MySQL.

---

**Project Name:** Vignan College ERP System  
**Application Type:** Academic Management / College ERP  
**Architecture:** React + Spring Boot REST API + MySQL  

