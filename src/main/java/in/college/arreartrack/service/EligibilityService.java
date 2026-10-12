package in.college.arreartrack.service;

import in.college.arreartrack.model.AcademicResult;
import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import java.util.List;

public final class EligibilityService {
    private final EligibilityRule rule;

    public EligibilityService(EligibilityRule rule) {
        if (rule == null) throw new IllegalArgumentException("Eligibility rule is required.");
        this.rule = rule;
    }

    public List<EligibilityResult> assessAll(PlacementDrive drive, List<Student> students, List<AcademicResult> results) {
        return students.stream().map(student -> {
            int arrears = (int) results.stream()
                    .filter(result -> result.studentId() == student.getId() && "ARREAR".equals(result.status()))
                    .count();
            return rule.evaluate(student, drive, arrears);
        }).toList();
    }
}
