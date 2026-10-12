# What to submit for ArrearTrack

Use your teacher's official template and submission channel if one was provided. This list is based on the requirements you shared: teams up to four, OOP + DBMS, core concepts, database connectivity/CRUD, project explanation, written documentation, screenshots, and a working demonstration.

## Recommended submission bundle

1. **Project report (printed/PDF, as instructed):** personalize `PROJECT_REPORT.md` and transfer it to the college template if required. The problem statement, objectives, requirements, design, schema, implementation, actual test results, limitations, references, and four real application screenshots are already included; add your institution, course, team/register numbers, guide, and academic year.
2. **Source code:** submit the complete `ArrearTrack` project folder or ZIP, including `src/`, `pom.xml`, `database/schema.sql`, `README.md`, and `docs/`. Do not submit only compiled files.
3. **Database script:** include `database/schema.sql` so the evaluator can recreate tables and fictional demo rows.
4. **Execution proof:** four verified screenshots are already in `docs/screenshots/` and embedded in the report. Label them as fictional prototype output if your template needs figure captions.
5. **Demonstration:** bring a laptop with JDK, Maven and MySQL installed, database loaded, and sample credentials configured locally. Test the app before the demo.
6. **Presentation/oral explanation:** prepare the 2–3 minute script below and be ready to show one create/edit/delete operation and an eligibility update.
7. **Team details:** list no more than four contributors and describe each member's contribution if your instructor requests it.

## Before submission — team checklist

- [ ] Replace every square-bracket placeholder in `PROJECT_REPORT.md` with your actual institution, course, guide, team member names/register numbers (maximum four), and academic year.
- [ ] Use your instructor's template and confirm the required format/channel (report, source ZIP, SQL file, live demo, and whether slides are wanted); do not assume unconfirmed requirements.
- [ ] On the computer you will use for evaluation, install JDK 21+, Maven, and MySQL 8+; load `database/schema.sql` and set `DB_URL`, `DB_USER`, and `DB_PASSWORD`. Never put a real database password in the ZIP or report.
- [ ] Run `mvn clean package` and `mvn exec:java` on that computer to confirm the environment and display work; this project already passed the same build and unit tests here.
- [ ] Rehearse all four tabs and one student create/edit/delete flow. The GUI CRUD path has already been tested against MySQL in this project.
- [ ] Rehearse changing `DEMO002` / `CS402` from `ARREAR` to `PASS` and refreshing SampleTech eligibility. The outcome was verified and the included database is restored to its seeded `ARREAR` state.
- [ ] The four real prototype screenshots are already in `docs/screenshots/` and in the report; take new ones only if your instructor's template asks for a different layout.
- [ ] Keep a backup copy of the source ZIP and SQL script.
- [ ] Make sure every team member understands the code and can explain their contribution.

## Suggested oral explanation (adapt in your own words)

> “Our project is ArrearTrack, a College Arrear and Placement Eligibility Tracker. The problem we selected is that pending subject arrears can affect campus placement eligibility, while students may need a clearer way to compare their current result status against the criteria of a specific drive. Our prototype stores student profiles, current subject results, and configurable drive rules in MySQL. It uses Java Swing for the interface and JDBC for database connectivity. The eligibility service counts current ARREAR rows, checks a configured minimum CGPA and maximum-arrear limit, and explains unmet conditions. We demonstrate OOP using encapsulation, inheritance from Person to Student, repository and eligibility abstractions, and a replaceable eligibility-rule implementation. The sample data is fictional; this is an academic prototype and does not connect to the official college portal.”

## Likely viva questions to practice

1. What is the problem statement, and who benefits from the prototype?
2. How does `Student` use inheritance? What does `Person` contain?
3. Where are encapsulation, abstraction, and polymorphism demonstrated?
4. Why use separate `students`, `subjects`, `academic_results`, and `placement_drives` tables?
5. What is a primary key, foreign key, unique key, and why is each used here?
6. How does JDBC connect Java to MySQL? Where are prepared statements used?
7. What happens when a student's result changes from `ARREAR` to `PASS`?
8. What does the eligibility rule do if CGPA is blank?
9. What assumptions and limitations does the prototype have?
10. How would you add full attempt history, authentication, or an additional recruiter rule?

## Explicitly confirmed vs. confirm locally

**Confirmed by the requirements you supplied:** team maximum four; OOP + DBMS; show OOP concepts, DB design, database connectivity and CRUD; explain title/problem/objectives/basic function; document requirements/design/schema/implementation/output screenshots; demonstrate and attend evaluation.  
**Still dependent on your instructor:** report template, exact file types, screenshot format, whether slides are required, title registration, citation style, and submission channel. Confirm those rather than guessing.
