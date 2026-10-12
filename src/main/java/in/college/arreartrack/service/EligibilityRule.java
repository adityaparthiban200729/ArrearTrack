package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;

/** Strategy interface: alternate rule sets can be added without changing the UI. */
public interface EligibilityRule {
    EligibilityResult evaluate(Student student, PlacementDrive drive, int activeArrears);
}
