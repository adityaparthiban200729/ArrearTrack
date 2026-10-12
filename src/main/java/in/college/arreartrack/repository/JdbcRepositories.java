package in.college.arreartrack.repository;

import in.college.arreartrack.db.Database;
import in.college.arreartrack.model.AcademicResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import in.college.arreartrack.model.Subject;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** JDBC adapter factory. SQL details stay behind the repository interfaces. */
public final class JdbcRepositories {
    private JdbcRepositories() { }
    public static StudentRepository students() { return new JdbcStudentRepository(); }
    public static AcademicResultRepository results() { return new JdbcAcademicResultRepository(); }
    public static PlacementDriveRepository drives() { return new JdbcPlacementDriveRepository(); }

    private static final class JdbcStudentRepository implements StudentRepository {
        @Override public List<Student> findAll() throws SQLException {
            String sql = "SELECT student_id, roll_no, full_name, email, department, semester, cgpa FROM students ORDER BY roll_no";
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<Student> rows = new ArrayList<>();
                while (rs.next()) rows.add(readStudent(rs));
                return rows;
            }
        }
        @Override public void save(Student s) throws SQLException {
            if (s.getId() == 0) {
                String sql = "INSERT INTO students (roll_no, full_name, email, department, semester, cgpa) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                    bindStudent(ps, s, 1); ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE students SET roll_no=?, full_name=?, email=?, department=?, semester=?, cgpa=? WHERE student_id=?";
                try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                    bindStudent(ps, s, 1); ps.setLong(7, s.getId());
                    if (ps.executeUpdate() == 0) throw new SQLException("Student was not found.");
                }
            }
        }
        @Override public void delete(long id) throws SQLException {
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE student_id=?")) {
                ps.setLong(1, id); if (ps.executeUpdate() == 0) throw new SQLException("Student was not found.");
            }
        }
        private static void bindStudent(PreparedStatement ps, Student s, int start) throws SQLException {
            ps.setString(start, s.getRollNumber()); ps.setString(start + 1, s.getFullName()); ps.setString(start + 2, s.getEmail());
            ps.setString(start + 3, s.getDepartment()); ps.setInt(start + 4, s.getSemester());
            if (s.getCgpa() == null) ps.setNull(start + 5, java.sql.Types.DECIMAL); else ps.setBigDecimal(start + 5, BigDecimal.valueOf(s.getCgpa()));
        }
    }

    private static final class JdbcAcademicResultRepository implements AcademicResultRepository {
        @Override public List<AcademicResult> findAll() throws SQLException {
            String sql = "SELECT r.result_id, r.student_id, r.subject_id, s.roll_no, s.full_name, sub.subject_code, sub.subject_name, r.status, r.attempts, r.updated_at " +
                    "FROM academic_results r JOIN students s ON s.student_id=r.student_id JOIN subjects sub ON sub.subject_id=r.subject_id " +
                    "ORDER BY s.roll_no, sub.subject_code";
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<AcademicResult> rows = new ArrayList<>();
                while (rs.next()) {
                    Timestamp updated = rs.getTimestamp("updated_at");
                    rows.add(new AcademicResult(rs.getLong("result_id"), rs.getLong("student_id"), rs.getLong("subject_id"),
                            rs.getString("roll_no"), rs.getString("full_name"), rs.getString("subject_code"), rs.getString("subject_name"),
                            rs.getString("status"), rs.getInt("attempts"), updated == null ? null : updated.toLocalDateTime()));
                }
                return rows;
            }
        }
        @Override public List<Student> findStudents() throws SQLException { return students().findAll(); }
        @Override public List<Subject> findSubjects() throws SQLException {
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT subject_id, subject_code, subject_name, semester FROM subjects ORDER BY subject_code"); ResultSet rs = ps.executeQuery()) {
                List<Subject> rows = new ArrayList<>();
                while (rs.next()) rows.add(new Subject(rs.getLong("subject_id"), rs.getString("subject_code"), rs.getString("subject_name"), rs.getInt("semester")));
                return rows;
            }
        }
        @Override public void save(long studentId, long subjectId, String status, int attempts) throws SQLException {
            String sql = "INSERT INTO academic_results (student_id, subject_id, status, attempts) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE status=VALUES(status), attempts=VALUES(attempts)";
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, studentId); ps.setLong(2, subjectId); ps.setString(3, status); ps.setInt(4, attempts); ps.executeUpdate();
            }
        }
        @Override public void delete(long id) throws SQLException {
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM academic_results WHERE result_id=?")) {
                ps.setLong(1, id); if (ps.executeUpdate() == 0) throw new SQLException("Result record was not found.");
            }
        }
    }

    private static final class JdbcPlacementDriveRepository implements PlacementDriveRepository {
        @Override public List<PlacementDrive> findAll() throws SQLException {
            String sql = "SELECT drive_id, company_name, job_role, min_cgpa, max_active_arrears, drive_date FROM placement_drives ORDER BY drive_date, company_name";
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                List<PlacementDrive> rows = new ArrayList<>();
                while (rs.next()) {
                    BigDecimal min = rs.getBigDecimal("min_cgpa");
                    Date date = rs.getDate("drive_date");
                    rows.add(new PlacementDrive(rs.getLong("drive_id"), rs.getString("company_name"), rs.getString("job_role"),
                            min == null ? null : min.doubleValue(), rs.getInt("max_active_arrears"), date == null ? null : date.toLocalDate()));
                }
                return rows;
            }
        }
        @Override public void save(PlacementDrive d) throws SQLException {
            String sql = d.id() == 0
                    ? "INSERT INTO placement_drives (company_name, job_role, min_cgpa, max_active_arrears, drive_date) VALUES (?, ?, ?, ?, ?)"
                    : "UPDATE placement_drives SET company_name=?, job_role=?, min_cgpa=?, max_active_arrears=?, drive_date=? WHERE drive_id=?";
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, d.companyName()); ps.setString(2, d.jobRole());
                if (d.minimumCgpa() == null) ps.setNull(3, java.sql.Types.DECIMAL); else ps.setBigDecimal(3, BigDecimal.valueOf(d.minimumCgpa()));
                ps.setInt(4, d.maximumActiveArrears());
                if (d.driveDate() == null) ps.setNull(5, java.sql.Types.DATE); else ps.setDate(5, Date.valueOf(d.driveDate()));
                if (d.id() != 0) { ps.setLong(6, d.id()); if (ps.executeUpdate() == 0) throw new SQLException("Placement drive was not found."); }
                else ps.executeUpdate();
            }
        }
        @Override public void delete(long id) throws SQLException {
            try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM placement_drives WHERE drive_id=?")) {
                ps.setLong(1, id); if (ps.executeUpdate() == 0) throw new SQLException("Placement drive was not found.");
            }
        }
    }

    private static Student readStudent(ResultSet rs) throws SQLException {
        BigDecimal value = rs.getBigDecimal("cgpa");
        return new Student(rs.getLong("student_id"), rs.getString("roll_no"), rs.getString("full_name"), rs.getString("email"),
                rs.getString("department"), rs.getInt("semester"), value == null ? null : value.doubleValue());
    }
}
