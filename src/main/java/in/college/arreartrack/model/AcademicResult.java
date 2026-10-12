package in.college.arreartrack.model;

import java.time.LocalDateTime;

public record AcademicResult(long id, long studentId, long subjectId, String rollNumber,
                             String studentName, String subjectCode, String subjectName,
                             String status, int attempts, LocalDateTime updatedAt) {
    public AcademicResult {
        if (id < 0 || studentId < 1 || subjectId < 1 || attempts < 1) throw new IllegalArgumentException("Invalid result data.");
        if (!"PASS".equals(status) && !"ARREAR".equals(status)) throw new IllegalArgumentException("Status must be PASS or ARREAR.");
    }
}
