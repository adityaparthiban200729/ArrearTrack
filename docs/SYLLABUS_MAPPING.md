# ArrearTrack syllabus coverage map

This map connects the supplied five-unit syllabus and project requirements to the working code, database, tests, and written documentation. The project is a Java 21 desktop prototype; syllabus concepts that are foundational language topics are demonstrated through the implementation and explained here rather than repeated as artificial tutorial screens.

## Unit I — Introduction to OOP and Java fundamentals

| Syllabus topic | ArrearTrack evidence |
|---|---|
| Introduction to object-oriented programming | Domain objects model students, subjects, results, drives, and eligibility outcomes; behavior is separated into UI, service, and repository layers. See `src/main/java/in/college/arreartrack/model/` and `docs/DESIGN.md`. |
| Overview of Java | Java 21, Swing, JDBC, Maven, and JUnit 5 are documented in `README.md`; `Main.java` launches the desktop application. |
| Java architecture | Maven compiles Java source to bytecode for the JVM; the MySQL Connector/J driver supplies the JDBC implementation at runtime. Build and launch commands are in `README.md`. |
| Data types | Primitive values (`int`, `long`, `boolean`) and reference types (`String`, `Double`, `LocalDate`, collections) appear throughout the model and service classes. |
| Variables | Constructor parameters, immutable record components, local variables, and private instance fields are used in the model, service, repository, and UI layers. |
| Operators | Comparisons and logical operators validate ranges and determine eligibility; arithmetic operators calculate totals and averages in `EligibilityAnalytics`. |

## Unit II — Classes and inheritance

| Syllabus topic | ArrearTrack evidence |
|---|---|
| Classes / class fundamentals / a simple class | `Student`, `Person`, `EligibilityService`, and repository classes show fields, constructors, and methods. |
| Declaring objects | `MainFrame` creates repository, service, analytics, exporter, table, and model objects with `new`; tests instantiate domain objects. |
| Methods | Examples include `EligibilityService.assessAll`, `StandardEligibilityRule.evaluate`, and repository `findAll` / `save` / `delete` methods. |
| Constructors | Domain constructors and compact record constructors validate incoming values. |
| Inheritance basics | `Student extends Person`. |
| Member access | `private` state, `protected` superclass construction, and public methods/types demonstrate access protection. |
| Method overriding | `Student.getRoleLabel`, `PlacementDrive.toString`, `Subject.toString`, and the Swing table-model `isCellEditable` override. |
| Abstract classes | `Person` is an abstract base class with an abstract role method. |
| Object class | All ordinary classes inherit from `Object`; `toString()` is overridden for readable subject and placement-drive labels, and records provide value-object behavior. |

## Unit III — Packages, interface and exception handling

| Syllabus topic | ArrearTrack evidence |
|---|---|
| Packages / defining a package | Source is organized under `in.college.arreartrack` subpackages: `db`, `model`, `repository`, `service`, and `ui`. |
| Access protection | `private`, `protected`, and `public` members are used to control access to state and APIs. |
| Imports | Project classes and Java/JDBC/Swing APIs are imported in each package. |
| Interfaces / implements | Repository contracts and `EligibilityRule` are interfaces; `JdbcRepositories`' adapters and `StandardEligibilityRule` implement them. |
| Nested interfaces | `EligibilityCsvExporter.OutcomeFormatter` is a public nested functional interface and is exercised by a custom-formatter test. |
| Exception types | Checked exceptions include `SQLException` and `IOException`; validation failures use unchecked `IllegalArgumentException`. |
| `try` / `catch` | JDBC resources and CSV writing use `try`; `Main` and `MainFrame` catch and report connection, SQL, and file errors. |
| `throw` / `throws` | Constructors and validation methods throw `IllegalArgumentException`; repository and file APIs declare checked exceptions with `throws`. |
| `finally` | `MainFrame.exportEligibilityCsv` re-enables the export button in `finally`, whether writing succeeds or fails. Resource cleanup uses try-with-resources. |

## Unit IV — I/O and collections

| Syllabus topic | ArrearTrack evidence |
|---|---|
| Input/output basics and streams | The Eligibility screen lets the user save a report to a selected path. |
| Byte streams and character streams | `EligibilityCsvExporter` writes bytes through `OutputStream`, encodes characters as UTF-8 with `OutputStreamWriter`, and buffers text with `BufferedWriter`. |
| Collection interfaces | `List` and `Map` are used in service and repository APIs. |
| Collection classes | `ArrayList` stores JDBC query results; `TreeMap` provides deterministic department summaries. |
| Iterator | The CSV exporter traverses assessed outcomes with `Iterator<EligibilityResult>`. |

## Unit V — Streams API, lambda and JDBC

| Syllabus topic | ArrearTrack evidence |
|---|---|
| Stream API | Eligibility assessment and analytics use the Java Stream API. |
| Reduction | `EligibilityAnalytics` uses `map(...).reduce(...)` for a count and `reduce` for total active arrears. |
| Parallel | `parallelStream()` computes total arrears and grouped department summaries. Results are combined using a collector. |
| Mapping | `map` converts eligibility outcomes into count values and student CGPA values; the CSV exporter maps outcomes to report rows with `Function`. |
| Collecting | `collect`, `Collectors.groupingBy`, and a downstream `Collector` aggregate the per-department metrics. |
| Lambda expressions | Swing actions, filtering, report formatting, and stream transformations use lambdas and method references. |
| Functional interfaces | `EligibilityCsvExporter.OutcomeFormatter` is a nested `@FunctionalInterface`; `MainFrame.SqlAction` is another project-defined functional interface. |
| Predefined functional interfaces | `Predicate<EligibilityResult>` and `Function<EligibilityResult, String[]>` are used in the analytics/export code; Swing listeners and `Runnable` also accept functional expressions. |
| Accessing databases with JDBC | `Database` obtains JDBC connections; `JdbcRepositories` uses `PreparedStatement`, `ResultSet`, and try-with-resources for MySQL CRUD and queries. |

## Teacher's project requirements

| Requirement | Project evidence / status |
|---|---|
| Demonstrate OOP | Abstract `Person`, inherited `Student`, encapsulated domain objects, repository/rule abstractions, and replaceable eligibility strategy. |
| Demonstrate DBMS and database design | Four-table MySQL schema with primary, foreign, unique, and check constraints; see `database/schema.sql` and `docs/DESIGN.md`. |
| Database connectivity | Environment-configured JDBC connection through `Database`; MySQL Connector/J dependency in `pom.xml`. |
| CRUD | Student, current-result, and placement-drive create/read/update/delete operations in Swing and repository implementations. |
| Documentation | Project report, design notes, setup guide, demo script, submission checklist, and this syllabus map. |
| Outputs / screenshots | Four fictional-data application screenshots in `docs/screenshots/`, embedded in the report. |
| Project demonstration | `docs/DEMO_SCRIPT.md` walks through CRUD, changing a result, eligibility evaluation, and CSV export. |
| Team | Aditya.p, Adithyaguhan.c, Aneesh.G, and Adithya.s. Add registration numbers and institutional details only if the instructor requires them. |

## Honest scope note

ArrearTrack demonstrates the syllabus through a practical application, not through separate lecture examples for every topic. It is a classroom prototype with fictional data, no authentication, and no official college/recruiter integration. See `README.md` for setup and known limitations.
