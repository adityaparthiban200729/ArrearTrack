package in.college.arreartrack.model;

import java.time.LocalDate;

public record PlacementDrive(long id, String companyName, String jobRole, Double minimumCgpa,
                             int maximumActiveArrears, LocalDate driveDate) {
    public PlacementDrive {
        if (id < 0 || maximumActiveArrears < 0) throw new IllegalArgumentException("Invalid drive ID or arrear limit.");
        if (companyName == null || companyName.isBlank() || jobRole == null || jobRole.isBlank())
            throw new IllegalArgumentException("Company and job role are required.");
        if (minimumCgpa != null && (minimumCgpa < 0 || minimumCgpa > 10))
            throw new IllegalArgumentException("Minimum CGPA must be between 0 and 10.");
        companyName = companyName.trim();
        jobRole = jobRole.trim();
    }
    @Override public String toString() { return companyName + " — " + jobRole; }
}
