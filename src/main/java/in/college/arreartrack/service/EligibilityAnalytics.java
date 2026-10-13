package in.college.arreartrack.service;

import in.college.arreartrack.model.EligibilityResult;
import in.college.arreartrack.model.Student;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/** Stream-based summaries used by the Eligibility screen and exported reports. */
public final class EligibilityAnalytics {
    private static final Predicate<EligibilityResult> IS_ELIGIBLE = EligibilityResult::eligible;

    public Summary summarize(List<EligibilityResult> outcomes) {
        List<EligibilityResult> safeOutcomes = List.copyOf(Objects.requireNonNull(outcomes, "Outcomes are required."));

        long eligibleCount = safeOutcomes.stream()
                .filter(IS_ELIGIBLE)
                .map(outcome -> 1L)
                .reduce(0L, Long::sum);
        int totalActiveArrears = safeOutcomes.parallelStream()
                .map(EligibilityResult::activeArrears)
                .reduce(0, Integer::sum);
        OptionalDouble average = safeOutcomes.stream()
                .map(EligibilityResult::student)
                .map(Student::getCgpa)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average();

        Map<String, DepartmentSummary> departments = safeOutcomes.parallelStream()
                .collect(Collectors.groupingBy(
                        outcome -> outcome.student().getDepartment(),
                        TreeMap::new,
                        DEPARTMENT_COLLECTOR));

        return new Summary(safeOutcomes.size(), Math.toIntExact(eligibleCount), totalActiveArrears,
                average.isPresent() ? average.getAsDouble() : null, departments);
    }

    private static final Collector<EligibilityResult, DepartmentAccumulator, DepartmentSummary> DEPARTMENT_COLLECTOR =
            Collector.of(DepartmentAccumulator::new, DepartmentAccumulator::add,
                    DepartmentAccumulator::combine, DepartmentAccumulator::finish);

    public record Summary(int studentCount, int eligibleCount, int totalActiveArrears,
                          Double averageCgpa, Map<String, DepartmentSummary> departments) {
        public Summary {
            departments = Collections.unmodifiableMap(new TreeMap<>(departments));
        }
    }

    public record DepartmentSummary(int studentCount, int eligibleCount, int totalActiveArrears,
                                    Double averageCgpa, int studentsWithCgpa) { }

    private static final class DepartmentAccumulator {
        private int studentCount;
        private int eligibleCount;
        private int totalActiveArrears;
        private int studentsWithCgpa;
        private double cgpaTotal;

        private void add(EligibilityResult outcome) {
            studentCount++;
            if (outcome.eligible()) eligibleCount++;
            totalActiveArrears += outcome.activeArrears();
            Double cgpa = outcome.student().getCgpa();
            if (cgpa != null) {
                cgpaTotal += cgpa;
                studentsWithCgpa++;
            }
        }

        private DepartmentAccumulator combine(DepartmentAccumulator other) {
            studentCount += other.studentCount;
            eligibleCount += other.eligibleCount;
            totalActiveArrears += other.totalActiveArrears;
            studentsWithCgpa += other.studentsWithCgpa;
            cgpaTotal += other.cgpaTotal;
            return this;
        }

        private DepartmentSummary finish() {
            Double average = studentsWithCgpa == 0 ? null : cgpaTotal / studentsWithCgpa;
            return new DepartmentSummary(studentCount, eligibleCount, totalActiveArrears,
                    average, studentsWithCgpa);
        }
    }
}
