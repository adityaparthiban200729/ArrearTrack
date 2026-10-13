package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Writes the current drive assessment as a UTF-8 CSV file. */
public final class EligibilityCsvExporter {
    private static final String[] HEADERS = {
            "Roll No.", "Student", "Department", "CGPA", "Active arrears", "Decision", "Explanation"
    };

    /** Allows callers to customize the exported columns without changing file-writing logic. */
    @FunctionalInterface
    public interface OutcomeFormatter {
        String[] format(EligibilityResult outcome);
    }

    public void export(Path target, List<EligibilityResult> outcomes) throws IOException {
        export(target, outcomes, EligibilityCsvExporter::defaultFields);
    }

    public void export(Path target, List<EligibilityResult> outcomes, OutcomeFormatter formatter) throws IOException {
        Objects.requireNonNull(target, "A destination path is required.");
        List<EligibilityResult> safeOutcomes = List.copyOf(Objects.requireNonNull(outcomes, "Outcomes are required."));
        Objects.requireNonNull(formatter, "An outcome formatter is required.");
        Function<EligibilityResult, String[]> rowMapper = formatter::format;

        // OutputStream writes bytes; OutputStreamWriter decodes Java characters into UTF-8 bytes.
        try (OutputStream byteStream = Files.newOutputStream(target);
             BufferedWriter characterStream = new BufferedWriter(
                     new OutputStreamWriter(byteStream, StandardCharsets.UTF_8))) {
            writeRecord(characterStream, HEADERS);
            Iterator<EligibilityResult> iterator = safeOutcomes.iterator();
            while (iterator.hasNext()) {
                writeRecord(characterStream, rowMapper.apply(iterator.next()));
            }
        }
    }

    private static String[] defaultFields(EligibilityResult outcome) {
        return new String[]{
                outcome.student().getRollNumber(),
                outcome.student().getFullName(),
                outcome.student().getDepartment(),
                outcome.student().getCgpa() == null ? "Not recorded" : String.format("%.2f", outcome.student().getCgpa()),
                Integer.toString(outcome.activeArrears()),
                outcome.eligible() ? "ELIGIBLE" : "NOT ELIGIBLE",
                outcome.reasonSummary()
        };
    }

    private static void writeRecord(BufferedWriter writer, String[] fields) throws IOException {
        String record = Arrays.stream(Objects.requireNonNull(fields, "A report row cannot be null."))
                .map(EligibilityCsvExporter::escapeCsv)
                .collect(Collectors.joining(","));
        writer.write(record);
        writer.newLine();
    }

    private static String escapeCsv(String rawValue) {
        String value = rawValue == null ? "" : rawValue;
        if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
            value = "'" + value;
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
