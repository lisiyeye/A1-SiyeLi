# A1-Siye Li - Same-Day Medical Appointment System

| | |
| --- | --- |
| Student | Siye Li (25258949) |
| Course work | PROG2004 - Object Oriented Programming, Assignment 1 |
| Language | Java, no third-party libraries |
| Entry point | `src/AssignmentOne.java` (the scripted demonstration) |

A class skeleton for a clinic that takes same-day appointments: health professionals of several
types, patients, appointments and a manager that keeps the appointment collection. There is no GUI,
no menu, no login and no database - the brief asks for the design, and `AssignmentOne` proves that
the design works by printing a scripted scenario.

## How to run

Windows one click:

```bat
run.bat
```

or, from any terminal with a JDK on `PATH`:

```bat
javac -encoding UTF-8 -Xlint:all -d bin src\*.java
java -cp bin AssignmentOne
```

Java 11 or newer is recommended (the code only needs Java 8 language features; it was compiled and
run with JDK 26).

## Class map

```text
HealthProfessional (abstract)
 |- GeneralPractitioner   implements Prescriber
 |- Surgeon               implements Prescriber
 |- Nurse
 '- Dietitian

Patient -----------------+
                         +--> Appointment (Comparable) --> AppointmentManager
HealthProfessional ------+                                     |
                                                               +--> ClinicSchedule
                                                               '--> OperationResult
```

## Files and their design point

| File | Kind | Responsibility and design point |
| --- | --- | --- |
| `HealthProfessional.java` | abstract class | Shared data (ID, name, weekly roster), all validation, `toString()` layout, identity by ID. Cannot be instantiated. |
| `GeneralPractitioner.java` | concrete type | Prescribing flag, consultation; implements `Prescriber`. |
| `Nurse.java` | concrete type | Certification level, blood sample; deliberately **not** a `Prescriber`. |
| `Surgeon.java` | concrete type | Surgical specialty, operation, post-operative prescription. |
| `Dietitian.java` | concrete type | Nutrition focus, meal plan. |
| `Prescriber.java` | interface | Capability that cuts across the hierarchy; static validation helper shared by both implementations. |
| `Patient.java` | immutable value class | Name and mobile number, both validated; identity is the mobile number. |
| `ClinicSchedule.java` | immutable configuration | Opening/closing time and slot length; generates the bookable slots. |
| `Appointment.java` | value class, `Comparable` | Patient + professional + date + time; validates itself; defines the chronological order. |
| `AppointmentManager.java` | collection manager | Add / view / filter / sort / cancel, duplicate detection, warning messages. |
| `OperationResult.java` | small result value | "Success with a message" or "warning message" returned by every manager operation. |
| `AssignmentOne.java` | demo script | Drives parts 3, 4, 5 and 6 of the brief and prints the results. |

## Design decisions (the questions the brief leaves open)

**1. Why is the base class abstract, and what does it force?**
`HealthProfessional` cannot be instantiated, and it declares three abstract methods:
`getRoleDescription()`, `describeAdditionalDetails()` and `performPrimaryDuty(Patient)`. Every
concrete type therefore has to say what it is, print its own extra data and perform its own duty -
the compiler enforces the behaviour that part 2 asks for.

**2. Why is the weekly roster an `EnumSet<DayOfWeek>`?**
A fixed set of weekdays is exactly what an `EnumSet` is for: compact, fast and in calendar order.
The constructor copies the caller's set and wraps it with `Collections.unmodifiableSet`, so external
code can read the roster but cannot add a day, remove a day or hand in a mutable set later.

**3. What does it mean for two professionals to be "the same", and how is it implemented?**
The numeric ID is unique inside the clinic, so `equals()` and `hashCode()` use the ID only. This is
implemented in `HealthProfessional`, which means every subclass inherits it. The appointment manager
keys its duplicate-check map by the professional object, so a second object with the same ID is
recognised as the same person - that is demonstrated in the `FAILURES` section of the demo.

**4. Why is the time a `LocalTime` and not a `String`?**
`"08:00"` compared as text only works while every string has the same shape, and it would break as
soon as the timetable uses a time such as `"09:45"`. `LocalTime` is a real time value: it compares by
clock order, it can be generated from the timetable and it prints as `08:00`. The date is stored as a
`LocalDate` as well, so "today only" can be checked.

**5. Why two collections in `AppointmentManager`?**
The appointments are kept in an `ArrayList` (small set, insertion order, sortable and easy to copy
for filtered views). The conflict check uses a second structure,
`Map<HealthProfessional, Set<LocalTime>>`: a `HashMap` looks the professional up in constant time and
a `HashSet` answers "is this slot taken" in constant time, instead of scanning every appointment.
Both structures are updated in the same methods, so the index cannot drift away from the list.

**6. How can the timetable be changed without touching the rest of the system?**
The timetable is an object, `ClinicSchedule`, not a set of constants. `Appointment` validates its
time against a schedule and `AppointmentManager` asks `getSchedule()` for every slot check. Passing
`new ClinicSchedule(LocalTime.of(9, 0), LocalTime.of(12, 0), 60)` to the manager is enough to switch
the whole clinic to three one-hour slots - the last part of the demo does exactly that.

**7. Why do failed operations return an `OperationResult` instead of printing a warning?**
The manager has no console code. It returns a message that says what happened, and the caller decides
where to show it. Unlike a `boolean` return value, the message travels with the result, so the demo
can print `[OK]` and `[WARNING]` lines with an explanation.

**8. Are the professionals' working days enforced?**
No, and this is deliberate. Part 5 of the brief lists two booking rules: the clinic timetable and no
double booking of the same professional in one slot. The roster is stored and can be queried with
`worksOn(DayOfWeek)` / `worksToday()`; if the clinic later wants to reject a booking on a day the
professional is not rostered, that is one extra test in `AppointmentManager.addAppointment`.

**9. What else keeps objects valid?**
Every field is `private final`; arguments are validated before they are stored (`IllegalArgumentException`
with a readable message); collections handed out are copies or unmodifiable views; `toString()` is
`final` in the base class so subclasses cannot break the shared layout. An illegal `Appointment`,
`Patient` or `HealthProfessional` simply cannot exist.

## Where each part of the brief is demonstrated

| Part of the brief | Where to look |
| --- | --- |
| 1 - health professional base class | `HealthProfessional`, printed in `demoHierarchy` |
| 2 - at least four concrete types | `GeneralPractitioner`, `Nurse`, `Surgeon`, `Dietitian`; printed in `demoHierarchy` |
| 3 - using the objects | `demoHierarchy`: one list, one loop, formatted output |
| 4 - appointment class | `Appointment`; printed and sorted in `demoAppointmentClass` |
| 5 - appointment collection | `AppointmentManager`, `ClinicSchedule`; driven in `demoAppointmentManager` |
| 6 - end-to-end demo | `demoAppointmentManager` and `demoScheduleReplacement` |
| 13 - style, Javadoc, comments | every class has Javadoc on the class, fields and methods plus inline comments |

The demo also shows, in order: duplicates, an appointment that is not for today, a missing
appointment, unknown professional ID, unknown phone number, an invalid slot, cancellation by ID and
time, cancellation by phone, a released slot being booked again and a replaced timetable.

## How to add a new type of professional

1. Create a subclass of `HealthProfessional` (or of an existing concrete type) and implement
   `getRoleDescription()`, `describeAdditionalDetails()` and `performPrimaryDuty(Patient)`.
2. Add `Prescriber` to the class declaration if the new type may prescribe medication.
3. Add one `professionals.add(new ...)` line in `AssignmentOne.createProfessionals()`.

Nothing else changes: the listing, the polymorphic loop, the interface loop, filtering, sorting and
the manager all work on the base type.

## The four pillars - revision notes for the video

| Pillar | Explanation | Where in the code |
| --- | --- | --- |
| Abstraction | Show only what matters, hide the detail, define a contract without an implementation. | `HealthProfessional` is abstract with abstract methods; `Prescriber` is an interface. |
| Encapsulation | Private state, validated input, controlled access. | All fields `private final`; validation helpers; unmodifiable roster; manager hides its lists and map. |
| Inheritance | Share the common code and let subclasses add what differs. | The four concrete types extend the base, call `super(...)` and only implement the three hooks. |
| Polymorphism | One type, many behaviours. | `performPrimaryDuty` in the demo loop; `Prescriber` via `instanceof`; `toString()` layout with subclass details. |

## Deliberately out of scope

- No user interface, menu, login, persistence or real users (explicitly excluded by the brief).
- Only professional/slot conflicts are rejected; a patient may hold two appointments at the same
  time, the brief does not forbid it.
- No past-time check: every appointment is for today and the clinic checks the slot grid, not the
  clock.
- Working days are recorded but not used to block a booking (see design decision 8).

## Submission checklist

- [ ] Zip the project as `A1-Siye Li.zip`
- [ ] Push the source to GitHub and put the repository link in the submission notes
- [ ] Add the marker as a collaborator if the repository is private
- [ ] WakaTime proof named `A1-Siye Li-WakaTime.png` (or `.jpg` / `.pdf`)
- [ ] Video of at most 5 minutes, shareable link in the submission notes
