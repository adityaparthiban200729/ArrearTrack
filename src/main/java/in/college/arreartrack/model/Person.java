package in.college.arreartrack.model;

/** Common identity data for people represented in the domain. */
public abstract class Person {
    private final long id;
    private final String fullName;
    private final String email;

    protected Person(long id, String fullName, String email) {
        if (id < 0) throw new IllegalArgumentException("ID cannot be negative.");
        this.id = id;
        this.fullName = requireText(fullName, "Name");
        this.email = requireText(email, "Email");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
        return value.trim();
    }

    public long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public abstract String getRoleLabel();
}
