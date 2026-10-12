package in.college.arreartrack.model;

public final class Student extends Person {
    private final String rollNumber;
    private final String department;
    private final int semester;
    private final Double cgpa;

    public Student(long id, String rollNumber, String fullName, String email,
                   String department, int semester, Double cgpa) {
        super(id, fullName, email);
        this.rollNumber = text(rollNumber, "Roll number");
        this.department = text(department, "Department");
        if (semester < 1 || semester > 8) throw new IllegalArgumentException("Semester must be between 1 and 8.");
        if (cgpa != null && (cgpa < 0 || cgpa > 10)) throw new IllegalArgumentException("CGPA must be between 0 and 10.");
        this.semester = semester;
        this.cgpa = cgpa;
    }

    private static String text(String value, String label) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required.");
        return value.trim();
    }

    @Override public String getRoleLabel() { return "Student"; }
    public String getRollNumber() { return rollNumber; }
    public String getDepartment() { return department; }
    public int getSemester() { return semester; }
    public Double getCgpa() { return cgpa; }
}
