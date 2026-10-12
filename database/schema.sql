-- ArrearTrack database (MySQL 8+). Run: mysql -u root -p < database/schema.sql
CREATE DATABASE IF NOT EXISTS arreartrack CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE arreartrack;

CREATE TABLE IF NOT EXISTS students (
  student_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  roll_no VARCHAR(20) NOT NULL UNIQUE,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(120) NOT NULL,
  department VARCHAR(80) NOT NULL,
  semester TINYINT NOT NULL,
  cgpa DECIMAL(3,2) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_student_semester CHECK (semester BETWEEN 1 AND 8),
  CONSTRAINT chk_student_cgpa CHECK (cgpa IS NULL OR (cgpa >= 0.00 AND cgpa <= 10.00))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS subjects (
  subject_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  subject_code VARCHAR(20) NOT NULL UNIQUE,
  subject_name VARCHAR(120) NOT NULL,
  semester TINYINT NOT NULL,
  CONSTRAINT chk_subject_semester CHECK (semester BETWEEN 1 AND 8)
) ENGINE=InnoDB;

-- One current result per student/subject. attempts is the count of exam attempts;
-- status is updated when the latest exam result is known (PASS or ARREAR).
CREATE TABLE IF NOT EXISTS academic_results (
  result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  status VARCHAR(10) NOT NULL,
  attempts SMALLINT UNSIGNED NOT NULL DEFAULT 1,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_result_student_subject UNIQUE (student_id, subject_id),
  CONSTRAINT chk_result_status CHECK (status IN ('PASS', 'ARREAR')),
  CONSTRAINT chk_result_attempts CHECK (attempts >= 1),
  CONSTRAINT fk_result_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
  CONSTRAINT fk_result_subject FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS placement_drives (
  drive_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  company_name VARCHAR(120) NOT NULL,
  job_role VARCHAR(120) NOT NULL,
  min_cgpa DECIMAL(3,2) NULL,
  max_active_arrears SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  drive_date DATE NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_drive_min_cgpa CHECK (min_cgpa IS NULL OR (min_cgpa >= 0.00 AND min_cgpa <= 10.00))
) ENGINE=InnoDB;

-- Seed rows are fictional examples for a classroom demonstration only.
INSERT IGNORE INTO students (roll_no, full_name, email, department, semester, cgpa) VALUES
('DEMO001', 'Aarav Nair', 'aarav.demo@example.test', 'Computer Science', 7, 8.10),
('DEMO002', 'Diya Kumar', 'diya.demo@example.test', 'Information Technology', 7, 7.45),
('DEMO003', 'Kavin Raj', 'kavin.demo@example.test', 'Computer Science', 6, 6.80),
('DEMO004', 'Meera Das', 'meera.demo@example.test', 'Electronics', 8, 9.05);

INSERT IGNORE INTO subjects (subject_code, subject_name, semester) VALUES
('CS301', 'Data Structures', 3), ('CS402', 'Database Management Systems', 4),
('CS601', 'Operating Systems', 6), ('IT502', 'Computer Networks', 5);

INSERT INTO academic_results (student_id, subject_id, status, attempts)
SELECT s.student_id, sub.subject_id,
       CASE WHEN s.roll_no = 'DEMO002' AND sub.subject_code = 'CS402' THEN 'ARREAR'
            WHEN s.roll_no = 'DEMO003' AND sub.subject_code = 'CS601' THEN 'ARREAR'
            ELSE 'PASS' END,
       CASE WHEN s.roll_no = 'DEMO002' AND sub.subject_code = 'CS402' THEN 2 ELSE 1 END
FROM students s CROSS JOIN subjects sub
WHERE s.roll_no IN ('DEMO001','DEMO002','DEMO003','DEMO004')
  AND sub.subject_code IN ('CS301','CS402','CS601','IT502')
ON DUPLICATE KEY UPDATE status = VALUES(status), attempts = VALUES(attempts);

INSERT INTO placement_drives (company_name, job_role, min_cgpa, max_active_arrears, drive_date)
SELECT 'SampleTech', 'Graduate Software Engineer', 7.00, 0, '2026-12-15'
WHERE NOT EXISTS (SELECT 1 FROM placement_drives WHERE company_name='SampleTech' AND job_role='Graduate Software Engineer');
INSERT INTO placement_drives (company_name, job_role, min_cgpa, max_active_arrears, drive_date)
SELECT 'Campus Labs', 'Junior QA Analyst', 6.50, 1, '2026-12-20'
WHERE NOT EXISTS (SELECT 1 FROM placement_drives WHERE company_name='Campus Labs' AND job_role='Junior QA Analyst');
