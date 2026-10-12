package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import java.util.ArrayList;
import java.util.List;

public final class StandardEligibilityRule implements EligibilityRule {
    @Override public EligibilityResult evaluate(Student student, PlacementDrive drive, int activeArrears) {
        if (activeArrears < 0) throw new IllegalArgumentException("Active arrears cannot be negative.");
        List<String> unmet = new ArrayList<>();
        if (drive.minimumCgpa() != null) {
            if (student.getCgpa() == null) unmet.add(String.format("CGPA not recorded (minimum %.2f)", drive.minimumCgpa()));
            else if (student.getCgpa() + 1e-9 < drive.minimumCgpa())
                unmet.add(String.format("CGPA %.2f is below %.2f", student.getCgpa(), drive.minimumCgpa()));
        }
        if (activeArrears > drive.maximumActiveArrears())
            unmet.add("Active arrears " + activeArrears + " exceed allowed " + drive.maximumActiveArrears());
        return new EligibilityResult(student, drive, activeArrears, unmet.isEmpty(), unmet);
    }
}
