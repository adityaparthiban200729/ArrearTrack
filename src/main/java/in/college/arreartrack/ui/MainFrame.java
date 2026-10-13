package in.college.arreartrack.ui;

import in.college.arreartrack.model.AcademicResult;
import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.PlacementDrive;
import in.college.arreartrack.model.Student;
import in.college.arreartrack.model.Subject;
import in.college.arreartrack.repository.AcademicResultRepository;
import in.college.arreartrack.repository.JdbcRepositories;
import in.college.arreartrack.repository.PlacementDriveRepository;
import in.college.arreartrack.repository.StudentRepository;
import in.college.arreartrack.service.EligibilityAnalytics;
import in.college.arreartrack.service.EligibilityCsvExporter;
import in.college.arreartrack.service.EligibilityService;
import in.college.arreartrack.service.StandardEligibilityRule;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public final class MainFrame extends JFrame {
    private final StudentRepository studentRepo = JdbcRepositories.students();
    private final AcademicResultRepository resultRepo = JdbcRepositories.results();
    private final PlacementDriveRepository driveRepo = JdbcRepositories.drives();
    private final EligibilityService eligibilityService = new EligibilityService(new StandardEligibilityRule());
    private final EligibilityAnalytics eligibilityAnalytics = new EligibilityAnalytics();
    private final EligibilityCsvExporter eligibilityCsvExporter = new EligibilityCsvExporter();
    private final JTextArea eligibilitySummary = new JTextArea(2, 40);
    private final JButton exportEligibilityButton = new JButton("Export CSV");

    private final DefaultTableModel studentsModel = model("ID", "Roll No.", "Name", "Email", "Department", "Semester", "CGPA");
    private final DefaultTableModel resultsModel = model("ID", "Roll No.", "Student", "Subject", "Subject name", "Status", "Attempts", "Last updated");
    private final DefaultTableModel drivesModel = model("ID", "Company", "Role", "Minimum CGPA", "Maximum arrears", "Drive date");
    private final DefaultTableModel eligibilityModel = model("Roll No.", "Student", "CGPA", "Active arrears", "Decision", "Rule explanation");

    private final JTable studentsTable = new JTable(studentsModel);
    private final JTable resultsTable = new JTable(resultsModel);
    private final JTable drivesTable = new JTable(drivesModel);
    private final JTable eligibilityTable = new JTable(eligibilityModel);
    private final JComboBox<PlacementDrive> driveSelector = new JComboBox<>();
    private List<Student> studentCache = List.of();
    private List<AcademicResult> resultCache = List.of();
    private List<PlacementDrive> driveCache = List.of();

    public MainFrame() {
        super("ArrearTrack | College Arrear & Placement Eligibility Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setSize(1120, 720);
        setColumnWidths(studentsTable, 50, 95, 140, 190, 170, 75, 70);
        setColumnWidths(resultsTable, 50, 90, 130, 75, 185, 75, 75, 170);
        setColumnWidths(drivesTable, 50, 155, 190, 110, 130, 115);
        setColumnWidths(eligibilityTable, 85, 145, 80, 115, 110, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 10));
        add(createHeader(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", createStudentsPanel());
        tabs.addTab("Results", createResultsPanel());
        tabs.addTab("Placement Drives", createDrivesPanel());
        tabs.addTab("Eligibility", createEligibilityPanel());
        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 3) refreshEligibility(); });
        add(tabs, BorderLayout.CENTER);
        add(new JLabel("Academic prototype • Demo data is fictional • Eligibility is advisory and depends on entered rules", SwingConstants.CENTER), BorderLayout.SOUTH);
        refreshStudents(); refreshResults(); refreshDrives();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 45, 77));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 13, 20));
        JLabel title = new JLabel("ArrearTrack"); title.setForeground(Color.WHITE); title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        JLabel subtitle = new JLabel("Track subject arrears. Understand placement-drive criteria."); subtitle.setForeground(new Color(220, 232, 245));
        JPanel text = new JPanel(new GridLayout(2, 1)); text.setOpaque(false); text.add(title); text.add(subtitle); panel.add(text, BorderLayout.WEST);
        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = basePanel();
        panel.add(toolbar("Student profiles", "Add student", "Edit selected", "Delete selected", this::addStudent, this::editStudent, this::deleteStudent), BorderLayout.NORTH);
        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = basePanel();
        panel.add(toolbar("Current subject results", "Add / update result", "Edit selected", "Delete selected", this::addResult, this::editResult, this::deleteResult), BorderLayout.NORTH);
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDrivesPanel() {
        JPanel panel = basePanel();
        panel.add(toolbar("Placement drives and configurable rules", "Add drive", "Edit selected", "Delete selected", this::addDrive, this::editDrive, this::deleteDrive), BorderLayout.NORTH);
        panel.add(new JScrollPane(drivesTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEligibilityPanel() {
        JPanel panel = basePanel();
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));
        top.add(new JLabel("Evaluate students against:"), BorderLayout.WEST);
        top.add(driveSelector, BorderLayout.CENTER);
        JPanel actions = new JPanel();
        JButton refresh = new JButton("Evaluate / refresh"); refresh.addActionListener(e -> refreshEligibility());
        exportEligibilityButton.addActionListener(e -> exportEligibilityCsv());
        actions.add(refresh); actions.add(exportEligibilityButton); top.add(actions, BorderLayout.EAST);
        JPanel info = new JPanel(new BorderLayout());
        info.add(top, BorderLayout.NORTH);
        JLabel note = new JLabel("Rules: minimum CGPA (if configured) and active arrears ≤ allowed maximum. This is an advisory prototype.");
        note.setBorder(BorderFactory.createEmptyBorder(0, 6, 9, 6)); info.add(note, BorderLayout.SOUTH);
        eligibilitySummary.setEditable(false);
        eligibilitySummary.setLineWrap(true);
        eligibilitySummary.setWrapStyleWord(true);
        eligibilitySummary.setOpaque(false);
        eligibilitySummary.setBorder(BorderFactory.createEmptyBorder(0, 6, 8, 6));
        JPanel heading = new JPanel(new BorderLayout());
        heading.add(info, BorderLayout.NORTH);
        heading.add(eligibilitySummary, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(eligibilityTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel basePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        return panel;
    }

    private JPanel toolbar(String heading, String add, String edit, String delete, Runnable onAdd, Runnable onEdit, Runnable onDelete) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(heading); label.setFont(label.getFont().deriveFont(Font.BOLD, 16f)); panel.add(label, BorderLayout.WEST);
        JPanel buttons = new JPanel();
        JButton bAdd = new JButton(add); bAdd.addActionListener(e -> onAdd.run());
        JButton bEdit = new JButton(edit); bEdit.addActionListener(e -> onEdit.run());
        JButton bDelete = new JButton(delete); bDelete.addActionListener(e -> onDelete.run());
        JButton bRefresh = new JButton("Refresh"); bRefresh.addActionListener(e -> refreshAll());
        buttons.add(bAdd); buttons.add(bEdit); buttons.add(bDelete); buttons.add(bRefresh);
        panel.add(buttons, BorderLayout.EAST); panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
        return panel;
    }

    private void refreshAll() { refreshStudents(); refreshResults(); refreshDrives(); refreshEligibility(); }

    private void refreshStudents() {
        runSafely(() -> {
            studentCache = studentRepo.findAll(); studentsModel.setRowCount(0);
            for (Student s : studentCache) studentsModel.addRow(new Object[]{s.getId(), s.getRollNumber(), s.getFullName(), s.getEmail(), s.getDepartment(), s.getSemester(), s.getCgpa() == null ? "—" : String.format("%.2f", s.getCgpa())});
        });
    }

    private void refreshResults() {
        runSafely(() -> {
            resultCache = resultRepo.findAll(); resultsModel.setRowCount(0);
            for (AcademicResult r : resultCache) resultsModel.addRow(new Object[]{r.id(), r.rollNumber(), r.studentName(), r.subjectCode(), r.subjectName(), r.status(), r.attempts(), r.updatedAt() == null ? "—" : r.updatedAt().toString().replace('T', ' ')});
        });
    }

    private void refreshDrives() {
        runSafely(() -> {
            Long selectedId = driveSelector.getSelectedItem() instanceof PlacementDrive current ? current.id() : null;
            driveCache = driveRepo.findAll(); drivesModel.setRowCount(0); driveSelector.removeAllItems();
            for (PlacementDrive d : driveCache) {
                drivesModel.addRow(new Object[]{d.id(), d.companyName(), d.jobRole(), d.minimumCgpa() == null ? "Not set" : String.format("%.2f", d.minimumCgpa()), d.maximumActiveArrears(), d.driveDate() == null ? "Not set" : d.driveDate()});
                driveSelector.addItem(d);
                if (selectedId != null && d.id() == selectedId) driveSelector.setSelectedItem(d);
            }
        });
    }

    private void refreshEligibility() {
        runSafely(() -> {
            eligibilityModel.setRowCount(0);
            if (!(driveSelector.getSelectedItem() instanceof PlacementDrive drive)) {
                eligibilitySummary.setText("Add or select a placement drive to evaluate students.");
                return;
            }
            studentCache = studentRepo.findAll(); resultCache = resultRepo.findAll();
            List<EligibilityResult> assessed = eligibilityService.assessAll(drive, studentCache, resultCache);
            eligibilitySummary.setText(formatSummary(eligibilityAnalytics.summarize(assessed)));
            for (EligibilityResult outcome : assessed) {
                Student s = outcome.student();
                eligibilityModel.addRow(new Object[]{s.getRollNumber(), s.getFullName(), s.getCgpa() == null ? "—" : String.format("%.2f", s.getCgpa()), outcome.activeArrears(), outcome.eligible() ? "ELIGIBLE" : "NOT ELIGIBLE", outcome.reasonSummary()});
            }
        });
    }

    private String formatSummary(EligibilityAnalytics.Summary summary) {
        String average = summary.averageCgpa() == null ? "—" : String.format("%.2f", summary.averageCgpa());
        StringBuilder departments = new StringBuilder("By department: ");
        summary.departments().forEach((name, values) -> {
            if (departments.length() > "By department: ".length()) departments.append("  |  ");
            departments.append(name).append(" — ").append(values.eligibleCount()).append('/')
                    .append(values.studentCount()).append(" eligible");
        });
        return "Eligible: " + summary.eligibleCount() + "/" + summary.studentCount()
                + "  •  Active arrears: " + summary.totalActiveArrears()
                + "  •  Average CGPA: " + average + "\n" + departments;
    }

    private void exportEligibilityCsv() {
        if (!(driveSelector.getSelectedItem() instanceof PlacementDrive drive)) {
            info("Select a placement drive before exporting its eligibility results.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        String safeCompanyName = drive.companyName().replaceAll("[^A-Za-z0-9_-]+", "-");
        chooser.setSelectedFile(new File("ArrearTrack-" + safeCompanyName + "-eligibility.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File destination = chooser.getSelectedFile();
        if (destination.exists() && !confirm("Replace the existing file?\n" + destination.getAbsolutePath())) return;

        exportEligibilityButton.setEnabled(false);
        try {
            List<EligibilityResult> assessed = eligibilityService.assessAll(
                    drive, studentRepo.findAll(), resultRepo.findAll());
            eligibilityCsvExporter.export(destination.toPath(), assessed);
            info("Exported " + assessed.size() + " eligibility rows to:\n" + destination.getAbsolutePath());
        } catch (SQLException | IOException ex) {
            error("Could not export the eligibility report: " + ex.getMessage());
        } finally {
            exportEligibilityButton.setEnabled(true);
        }
    }

    private void addStudent() { editStudentDialog(null); }
    private void editStudent() {
        int row = studentsTable.getSelectedRow();
        if (row < 0 || row >= studentCache.size()) { info("Select a student first."); return; }
        editStudentDialog(studentCache.get(row));
    }
    private void editStudentDialog(Student old) {
        JTextField roll = field(old == null ? "" : old.getRollNumber());
        JTextField name = field(old == null ? "" : old.getFullName());
        JTextField email = field(old == null ? "" : old.getEmail());
        JTextField department = field(old == null ? "" : old.getDepartment());
        JSpinner semester = new JSpinner(new SpinnerNumberModel(old == null ? 1 : old.getSemester(), 1, 8, 1));
        JTextField cgpa = field(old == null || old.getCgpa() == null ? "" : old.getCgpa().toString());
        JPanel form = form("Roll number", roll, "Full name", name, "Email", email, "Department", department, "Semester (1–8)", semester, "CGPA (0–10; optional)", cgpa);
        if (JOptionPane.showConfirmDialog(this, form, old == null ? "Add student" : "Edit student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            String mail = email.getText().trim(); if (!mail.contains("@")) throw new IllegalArgumentException("Enter a valid-looking email address.");
            Double grade = cgpa.getText().isBlank() ? null : Double.parseDouble(cgpa.getText().trim());
            studentRepo.save(new Student(old == null ? 0 : old.getId(), roll.getText(), name.getText(), mail, department.getText(), (Integer) semester.getValue(), grade));
            refreshStudents(); refreshResults(); refreshEligibility();
        } catch (Exception ex) { error("Could not save student: " + ex.getMessage()); }
    }
    private void deleteStudent() {
        Student s = selected(studentsTable, studentCache, "student"); if (s == null) return;
        if (confirm("Delete " + s.getRollNumber() + "? Its result rows will also be deleted.")) runSafely(() -> { studentRepo.delete(s.getId()); refreshAll(); });
    }

    private void addResult() { editResultDialog(null); }
    private void editResult() {
        AcademicResult r = selected(resultsTable, resultCache, "result"); if (r != null) editResultDialog(r);
    }
    private void editResultDialog(AcademicResult old) {
        try {
            List<Student> students = resultRepo.findStudents(); List<Subject> subjects = resultRepo.findSubjects();
            if (students.isEmpty() || subjects.isEmpty()) { info("Add at least one student and load subjects from database/schema.sql first."); return; }
            JComboBox<StudentOption> student = new JComboBox<>(students.stream().map(StudentOption::new).toArray(StudentOption[]::new));
            JComboBox<Subject> subject = new JComboBox<>(subjects.toArray(Subject[]::new));
            if (old != null) {
                for (int i = 0; i < student.getItemCount(); i++) if (student.getItemAt(i).student().getId() == old.studentId()) student.setSelectedIndex(i);
                for (int i = 0; i < subject.getItemCount(); i++) if (subject.getItemAt(i).id() == old.subjectId()) subject.setSelectedIndex(i);
            }
            JComboBox<String> status = new JComboBox<>(new String[]{"PASS", "ARREAR"}); if (old != null) status.setSelectedItem(old.status());
            JSpinner attempts = new JSpinner(new SpinnerNumberModel(old == null ? 1 : old.attempts(), 1, 1000, 1));
            JPanel form = form("Student", student, "Subject", subject, "Current result", status, "Attempt count", attempts);
            if (JOptionPane.showConfirmDialog(this, form, old == null ? "Add / update result" : "Edit current result", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            resultRepo.save(((StudentOption) student.getSelectedItem()).student().getId(), ((Subject) subject.getSelectedItem()).id(), (String) status.getSelectedItem(), (Integer) attempts.getValue());
            refreshResults(); refreshEligibility();
        } catch (Exception ex) { error("Could not save result: " + ex.getMessage()); }
    }
    private void deleteResult() {
        AcademicResult r = selected(resultsTable, resultCache, "result"); if (r != null && confirm("Delete this current result row?")) runSafely(() -> { resultRepo.delete(r.id()); refreshResults(); refreshEligibility(); });
    }

    private void addDrive() { editDriveDialog(null); }
    private void editDrive() {
        PlacementDrive d = selected(drivesTable, driveCache, "placement drive"); if (d != null) editDriveDialog(d);
    }
    private void editDriveDialog(PlacementDrive old) {
        JTextField company = field(old == null ? "" : old.companyName());
        JTextField role = field(old == null ? "" : old.jobRole());
        JTextField minCgpa = field(old == null || old.minimumCgpa() == null ? "" : old.minimumCgpa().toString());
        JSpinner maxArrears = new JSpinner(new SpinnerNumberModel(old == null ? 0 : old.maximumActiveArrears(), 0, 100, 1));
        JTextField date = field(old == null || old.driveDate() == null ? "" : old.driveDate().toString());
        JPanel form = form("Company", company, "Job role", role, "Minimum CGPA (optional)", minCgpa, "Maximum active arrears", maxArrears, "Drive date YYYY-MM-DD (optional)", date);
        if (JOptionPane.showConfirmDialog(this, form, old == null ? "Add placement drive" : "Edit placement drive", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            Double min = minCgpa.getText().isBlank() ? null : Double.parseDouble(minCgpa.getText().trim());
            LocalDate driveDate = date.getText().isBlank() ? null : LocalDate.parse(date.getText().trim());
            driveRepo.save(new PlacementDrive(old == null ? 0 : old.id(), company.getText(), role.getText(), min, (Integer) maxArrears.getValue(), driveDate));
            refreshDrives(); refreshEligibility();
        } catch (Exception ex) { error("Could not save placement drive: " + ex.getMessage()); }
    }
    private void deleteDrive() {
        PlacementDrive d = selected(drivesTable, driveCache, "placement drive"); if (d != null && confirm("Delete drive “" + d + "”?")) runSafely(() -> { driveRepo.delete(d.id()); refreshDrives(); refreshEligibility(); });
    }

    private <T> T selected(JTable table, List<T> rows, String label) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= rows.size()) { info("Select a " + label + " first."); return null; }
        return rows.get(row);
    }
    private static void setColumnWidths(JTable table, int... widths) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }
    private static DefaultTableModel model(String... columns) { return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } }; }
    private static JTextField field(String value) { return new JTextField(value, 22); }
    private static JPanel form(Object... labelsAndComponents) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8)); panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < labelsAndComponents.length; i += 2) { panel.add(new JLabel(String.valueOf(labelsAndComponents[i]))); panel.add((java.awt.Component) labelsAndComponents[i + 1]); }
        return panel;
    }
    private boolean confirm(String text) { return JOptionPane.showConfirmDialog(this, text, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION; }
    private void info(String text) { JOptionPane.showMessageDialog(this, text, "ArrearTrack", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String text) { JOptionPane.showMessageDialog(this, text, "ArrearTrack — error", JOptionPane.ERROR_MESSAGE); }
    private void runSafely(SqlAction action) { try { action.run(); } catch (SQLException ex) { error(ex.getMessage()); } }
    @FunctionalInterface private interface SqlAction { void run() throws SQLException; }
    private record StudentOption(Student student) { @Override public String toString() { return student.getRollNumber() + " — " + student.getFullName(); } }
}
