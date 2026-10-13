package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibilityCsvExporterTest {
    @TempDir
    Path temporaryDirectory;

    private final EligibilityCsvExporter exporter = new EligibilityCsvExporter();

    @Test
    void exportsUtf8CsvAndEscapesQuotesAndCommas() throws Exception {
        Student student = new Student(1, "D001", "Aditya, \"A\"", "aditya@example.test",
                "Computer Science", 7, 8.25);
        PlacementDrive drive = new PlacementDrive(1, "Demo Co", "Trainee", 7.0, 0,
                LocalDate.of(2026, 12, 1));
        EligibilityResult outcome = new StandardEligibilityRule().evaluate(student, drive, 0);
        Path destination = temporaryDirectory.resolve("eligibility.csv");

        exporter.export(destination, List.of(outcome));

        String csv = Files.readString(destination);
        assertTrue(csv.startsWith("\"Roll No.\",\"Student\",\"Department\""));
        assertTrue(csv.contains("\"Aditya, \"\"A\"\"\""));
        assertTrue(csv.contains("\"ELIGIBLE\""));
    }

    @Test
    void supportsCustomRowsThroughNestedFunctionalInterface() throws Exception {
        Student student = new Student(2, "D002", "ஆர்யா", "demo@example.test",
                "Information Technology", 7, 7.5);
        PlacementDrive drive = new PlacementDrive(1, "Demo Co", "Trainee", 7.0, 0,
                LocalDate.of(2026, 12, 1));
        EligibilityResult outcome = new StandardEligibilityRule().evaluate(student, drive, 0);
        Path destination = temporaryDirectory.resolve("custom.csv");
        EligibilityCsvExporter.OutcomeFormatter formatter = result ->
                new String[]{result.student().getRollNumber(), result.student().getFullName()};

        exporter.export(destination, List.of(outcome), formatter);

        String csv = Files.readString(destination);
        assertTrue(csv.contains("ஆர்யா"));
        assertTrue(csv.contains("\"D002\",\"ஆர்யா\""));
    }
}
