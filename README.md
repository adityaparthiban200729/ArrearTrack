# ArrearTrack — College Arrear and Placement Eligibility Tracker

A semester-project prototype demonstrating **OOP, MySQL database design, JDBC connectivity, CRUD, and configurable placement eligibility checks**. The desktop application is built with Java Swing and uses fictional sample data.

> **Scope note:** ArrearTrack is an academic prototype, not an official college placement system. It does not connect to a college portal or certify real recruiter rules. Use only fictional or anonymized records unless your college explicitly approves otherwise.

## Features

- Add, view, edit, and delete student profiles.
- Add, view, update, and delete a student's current subject-result status and attempt count.
- Create, view, update, and delete placement drives with a minimum CGPA and maximum active-arrear rule.
- Evaluate all saved students against a selected drive; show eligibility and each unmet condition.
- Summarize eligible counts, active arrears, average CGPA, and department-level outcomes using Java Streams.
- Export the selected drive's current eligibility results as a UTF-8 CSV report.
- Validate basic inputs and protect relational integrity with keys and constraints.

## Team

- Aditya.p
- Adithyaguhan.c
- Aneesh.G
- Adithya.s

Add registration numbers or institutional details only if the instructor's template requires them.

## Requirements

- JDK 21 or newer
- Maven 3.8+
- MySQL Server 8.0+
- A desktop environment capable of displaying Java Swing windows

## Setup

1. Start MySQL and create/load the database and fictional demo rows:

   ```bash
   mysql -u root -p < database/schema.sql
   ```

2. Configure the database connection. Defaults are `jdbc:mysql://localhost:3306/arreartrack`, user `root`, and an empty password. Override them in the environment (recommended):

   ```bash
   export DB_URL='jdbc:mysql://localhost:3306/arreartrack?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
   export DB_USER='your_mysql_user'
   export DB_PASSWORD='your_mysql_password'
   ```

   Use a database account limited to this project's database rather than a production administrator account.

3. From the project directory, run tests and launch the application:

   ```bash
   mvn test
   mvn exec:java
   ```

   To build a JAR: `mvn package`. The MySQL driver is a Maven dependency; `mvn exec:java` includes it automatically.

## First demonstration

1. Open the **Students** tab and review the fictional `DEMO00x` profiles.
2. Open **Results**; `DEMO002` has a sample arrear and a second attempt.
3. Select **SampleTech — Graduate Software Engineer** in **Eligibility**. It requires CGPA ≥ 7.00 and zero active arrears.
4. Select **Campus Labs — Junior QA Analyst** to demonstrate a different rule (CGPA ≥ 6.50 and at most one active arrear).
5. Point out the overall and department summary, then use **Export CSV** to save the current results.
6. Edit `DEMO002`'s `CS402` result to `PASS`, refresh eligibility, and show the changed outcome.
7. Create a temporary student/result/drive to demonstrate CREATE and DELETE, then remove it.

Do not enter actual personal data for a class demonstration unless permission and the appropriate data safeguards are in place.

## Architecture (OOP + DBMS)

- `Person` is an abstract base class; `Student` inherits common identity fields and adds academic data.
- `EligibilityRule` is an interface implemented by `StandardEligibilityRule`; the service evaluates via the interface.
- `StudentRepository`, `AcademicResultRepository`, and `PlacementDriveRepository` abstract persistence. JDBC-backed implementations provide the database operations.
- `EligibilityService` contains domain decision logic, separated from the Swing UI and SQL layer.
- MySQL tables: `students`, `subjects`, `academic_results`, and `placement_drives`. `academic_results` uses a unique `(student_id, subject_id)` key to represent the latest known result for each student/subject.

See [`docs/PROJECT_REPORT.md`](docs/PROJECT_REPORT.md), [`docs/DESIGN.md`](docs/DESIGN.md), and [`docs/SUBMISSION_CHECKLIST.md`](docs/SUBMISSION_CHECKLIST.md).
The syllabus coverage map is in [`docs/SYLLABUS_MAPPING.md`](docs/SYLLABUS_MAPPING.md).

## Known limitations / future work

- One current result per student/subject; a full attempt-by-attempt history is not stored.
- Single-user local prototype; no authentication, role-based permissions, audit log, notifications, CSV import, or official integrations.
- Eligibility reflects only the drive rules entered by the operator. Actual recruiters may add criteria not modelled here.
- Sample records are fictional and must not be described as college statistics.
