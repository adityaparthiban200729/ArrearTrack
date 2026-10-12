package in.college.arreartrack.model;

public record Subject(long id, String code, String name, int semester) {
    public Subject {
        if (id < 0 || semester < 1 || semester > 8) throw new IllegalArgumentException("Invalid subject ID or semester.");
        if (code == null || code.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("Subject code and name are required.");
        code = code.trim();
        name = name.trim();
    }
    @Override public String toString() { return code + " — " + name; }
}
