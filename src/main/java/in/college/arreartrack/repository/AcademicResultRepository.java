package in.college.arreartrack.repository;

import in.college.arreartrack.model.AcademicResult;
import in.college.arreartrack.model.Student;
import in.college.arreartrack.model.Subject;
import java.sql.SQLException;
import java.util.List;

public interface AcademicResultRepository {
    List<AcademicResult> findAll() throws SQLException;
    List<Student> findStudents() throws SQLException;
    List<Subject> findSubjects() throws SQLException;
    void save(long studentId, long subjectId, String status, int attempts) throws SQLException;
    void delete(long resultId) throws SQLException;
}
