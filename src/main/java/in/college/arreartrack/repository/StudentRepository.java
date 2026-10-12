package in.college.arreartrack.repository;

import in.college.arreartrack.model.Student;
import java.sql.SQLException;
import java.util.List;

public interface StudentRepository {
    List<Student> findAll() throws SQLException;
    void save(Student student) throws SQLException;
    void delete(long studentId) throws SQLException;
}
