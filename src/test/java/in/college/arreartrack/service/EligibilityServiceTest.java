package in.college.arreartrack.service;

import in.college.arreartrack.model.AcademicResult;
import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EligibilityServiceTest {
    private final StandardEligibilityRule rule = new StandardEligibilityRule();
    private final EligibilityService service = new EligibilityService(rule);

    @Test void eligibleWhenCgpaAndArrearsMeetTheDriveRule() {
        Student student = student(1, 8.0);
        EligibilityResult result = rule.evaluate(student, drive(0, 7.0), 0);
        assertTrue(result.eligible());
        assertTrue(result.unmetRequirements().isEmpty());
    }

    @Test void reportsBothCgpaAndArrearFailures() {
        Student student = student(1, 6.5);
        EligibilityResult result = rule.evaluate(student, drive(0, 7.0), 2);
        assertFalse(result.eligible());
        assertEquals(2, result.unmetRequirements().size());
        assertTrue(result.reasonSummary().contains("below 7.00"));
        assertTrue(result.reasonSummary().contains("exceed allowed 0"));
    }

    @Test void missingCgpaFailsOnlyWhenTheDriveRequiresIt() {
        Student student = student(1, null);
        assertFalse(rule.evaluate(student, drive(0, 6.0), 0).eligible());
        assertTrue(rule.evaluate(student, drive(0, null), 0).eligible());
    }

    @Test void serviceCountsOnlyArrearRowsBelongingToThatStudent() {
        Student first = student(1, 8.0);
        Student second = student(2, 8.0);
        List<AcademicResult> results = List.of(
                result(1, 1, "ARREAR"), result(2, 1, "PASS"), result(3, 2, "PASS"));
        List<EligibilityResult> outcomes = service.assessAll(drive(0, 7.0), List.of(first, second), results);
        assertEquals(2, outcomes.size());
        assertEquals(1, outcomes.get(0).activeArrears());
        assertEquals(0, outcomes.get(1).activeArrears());
        assertFalse(outcomes.get(0).eligible());
        assertTrue(outcomes.get(1).eligible());
    }

    private static Student student(long id, Double cgpa) {
        return new Student(id, "D" + id, "Demo Student", "demo@example.test", "Computer Science", 7, cgpa);
    }
    private static PlacementDrive drive(int arrears, Double cgpa) {
        return new PlacementDrive(1, "Example Co", "Trainee", cgpa, arrears, LocalDate.of(2026, 12, 1));
    }
    private static AcademicResult result(long id, long studentId, String status) {
        return new AcademicResult(id, studentId, id, "D" + studentId, "Demo Student", "CS" + id, "Subject", status, 1, LocalDateTime.now());
    }
}
