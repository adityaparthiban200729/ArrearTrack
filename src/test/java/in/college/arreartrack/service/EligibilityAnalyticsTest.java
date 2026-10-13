package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EligibilityAnalyticsTest {
    private final EligibilityAnalytics analytics = new EligibilityAnalytics();
    private final StandardEligibilityRule rule = new StandardEligibilityRule();
    private final PlacementDrive drive = new PlacementDrive(1, "Demo Co", "Trainee", 7.0, 1,
            LocalDate.of(2026, 12, 1));

    @Test
    void summarizesEligibilityArrearsAndAverageCgpa() {
        List<EligibilityResult> outcomes = List.of(
                outcome(1, "Computer Science", 8.0, 0),
                outcome(2, "Computer Science", 6.0, 2),
                outcome(3, "Information Technology", 7.5, 1),
                outcome(4, "Electronics", null, 0));

        EligibilityAnalytics.Summary summary = analytics.summarize(outcomes);

        assertEquals(4, summary.studentCount());
        assertEquals(2, summary.eligibleCount());
        assertEquals(3, summary.totalActiveArrears());
        assertEquals(7.1666666667, summary.averageCgpa(), 0.000001);
        assertEquals(2, summary.departments().get("Computer Science").studentCount());
        assertEquals(1, summary.departments().get("Computer Science").eligibleCount());
        assertEquals(7.0, summary.departments().get("Computer Science").averageCgpa(), 0.000001);
        assertNull(summary.departments().get("Electronics").averageCgpa());
    }

    @Test
    void emptySummaryHasZeroCountsAndNoAverage() {
        EligibilityAnalytics.Summary summary = analytics.summarize(List.of());
        assertEquals(0, summary.studentCount());
        assertEquals(0, summary.eligibleCount());
        assertEquals(0, summary.totalActiveArrears());
        assertNull(summary.averageCgpa());
        assertEquals(0, summary.departments().size());
    }

    private EligibilityResult outcome(long id, String department, Double cgpa, int arrears) {
        Student student = new Student(id, "D" + id, "Demo Student " + id,
                "demo@example.test", department, 7, cgpa);
        return rule.evaluate(student, drive, arrears);
    }
}
