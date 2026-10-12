# ArrearTrack live demo (about 4 minutes)

## Before class
- Start MySQL; ensure `arreartrack` exists and has sample rows from `database/schema.sql`.
- Set DB environment variables if defaults do not match.
- Run `mvn test`, then `mvn exec:java`.
- Keep the report screenshots ready as backup, but demonstrate the live application if possible.

## Walkthrough
1. **Introduce (20 sec):** “This is a Java Swing and MySQL prototype. The demo records are fictional. It does not connect to a college portal.”
2. **Students (40 sec):** point to the student profiles, add a temporary demo profile, edit a field, then delete it. Mention the unique roll-number constraint.
3. **Results (50 sec):** show the PASS and ARREAR rows. Edit `DEMO002` / `CS402` from `ARREAR` to `PASS`; note that one current row exists per student-subject pair and its attempt count is tracked.
4. **Placement Drives (40 sec):** compare the SampleTech (CGPA ≥ 7.00, zero arrears) and Campus Labs (CGPA ≥ 6.50, up to one arrear) configurations.
5. **Eligibility (60 sec):** select SampleTech and point to each outcome and explanation. Switch to Campus Labs to show the rule change.
6. **Show change (30 sec):** re-evaluate after the result update and show the decision changing where appropriate.
7. **Explain design (30 sec):** point out `Person` → `Student`, repository interfaces/JDBC implementations, and `EligibilityRule` / `StandardEligibilityRule`.

## Expected sample behavior
- `DEMO001`: no arrears, CGPA 8.10 — meets both sample drives.
- `DEMO002`: one arrear, CGPA 7.45 — fails SampleTech's zero-arrear rule, meets Campus Labs' stated thresholds.
- `DEMO003`: one arrear, CGPA 6.80 — fails SampleTech's thresholds, meets Campus Labs' thresholds.
- `DEMO004`: no arrears, CGPA 9.05 — meets both sample drives.

If the sample rows have been edited during rehearsal, restore them by re-running the seed statements in `database/schema.sql` (the demo upsert resets sample results) or recreate the database before the final demonstration.
