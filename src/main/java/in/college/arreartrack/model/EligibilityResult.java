package in.college.arreartrack.model;

import java.util.List;

public record EligibilityResult(Student student, PlacementDrive drive, int activeArrears,
                                boolean eligible, List<String> unmetRequirements) {
    public EligibilityResult {
        if (student == null || drive == null || activeArrears < 0) throw new IllegalArgumentException("Invalid eligibility result.");
        unmetRequirements = List.copyOf(unmetRequirements);
    }
    public String reasonSummary() { return eligible ? "Meets configured rules" : String.join("; ", unmetRequirements); }
}
