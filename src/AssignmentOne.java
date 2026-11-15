import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * PROG2004 Assignment 1 - same-day medical appointment system.
 *
 * <p>This class is the scripted demonstration asked for in the brief: it creates
 * the objects, drives the behaviour and prints the results, covering parts 1 to
 * 6 in order. It is not an application with a menu or a user interface, it is a
 * proof that the class skeleton works.</p>
 *
 * <ul>
 *   <li>Part 1 and 2: the abstract {@link HealthProfessional} hierarchy and its
 *       concrete types.</li>
 *   <li>Part 3: every object is stored as the base type and printed in one loop.</li>
 *   <li>Part 4: a single {@link Appointment} and its natural ordering.</li>
 *   <li>Part 5 and 6: the {@link AppointmentManager}, including duplicate
 *       bookings, filtering, sorting, cancelling and the warning messages.</li>
 * </ul>
 *
 * @author Siye Li (25258949)
 */
public class AssignmentOne {

    /** Wide separator used between the parts of the demo. */
    private static final String RULE = "======================================================================";

    /** Thin separator used inside a part. */
    private static final String THIN = "----------------------------------------------------------------------";

    /**
     * Prevents instantiation: this class is only a script for the demonstration.
     */
    private AssignmentOne() {
    }

    /**
     * Runs the whole demonstration.
     *
     * @param args ignored; the demo takes no command line arguments
     */
    public static void main(String[] args) {
        List<HealthProfessional> professionals = createProfessionals();

        Patient tom = new Patient("Tom", "0400111111");
        Patient lily = new Patient("Lily", "0400222222");
        Patient jack = new Patient("Jack", "0400333333");

        demoHierarchy(professionals, tom);
        demoAppointmentClass(professionals, tom, lily, jack);
        demoAppointmentManager(professionals, tom, lily, jack);
        demoScheduleReplacement(professionals, tom, lily, jack);

        System.out.println();
        System.out.println(RULE);
        System.out.println("END OF DEMO - every part of the brief was exercised.");
        System.out.println(RULE);
    }

    /**
     * Creates the staff of the clinic: three general practitioners, one nurse,
     * one surgeon and one dietitian. All of them are stored as the abstract base
     * type, which is what makes the loops in the demo polymorphic.
     *
     * <p>Adding a new type of professional later only needs a new subclass and
     * one more {@code add(...)} call here: the demo loops below do not change.</p>
     *
     * @return the professionals of the clinic
     */
    private static List<HealthProfessional> createProfessionals() {
        Set<DayOfWeek> mondayWednesday = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        Set<DayOfWeek> tuesdayThursday = EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);

        List<HealthProfessional> professionals = new ArrayList<>();
        // Three GPs; the third one is a registrar who may not prescribe on his own.
        professionals.add(new GeneralPractitioner(101, "Dr Amy", mondayWednesday, true));
        professionals.add(new GeneralPractitioner(102, "Dr Bob", tuesdayThursday, true));
        // No roster supplied on purpose: the convenience constructor uses the
        // default Monday-to-Friday week.
        professionals.add(new GeneralPractitioner(103, "Dr Chris", false));
        // One of each of the other concrete types.
        professionals.add(new Nurse(201, "Nurse Lisa", tuesdayThursday, "Level 3"));
        professionals.add(new Surgeon(301, "Dr Sam", mondayWednesday, "Cardiothoracic Surgery"));
        professionals.add(new Dietitian(401, "Ms Zoe", "Child Nutrition"));
        return professionals;
    }

    /**
     * Demonstrates parts 1 to 3: the shared base class, the four concrete types,
     * polymorphic behaviour, the {@link Prescriber} interface and the identity
     * rule used by the appointment manager.
     *
     * @param professionals staff of the clinic
     * @param patient       patient used for the behaviour calls
     */
    private static void demoHierarchy(List<HealthProfessional> professionals, Patient patient) {
        printHeader("PART 1 + 2 - ONE BASE TYPE, FOUR CONCRETE TYPES");

        System.out.println("Every object below is stored as HealthProfessional, yet each one prints");
        System.out.println("its own role and its own additional data:");
        for (HealthProfessional professional : professionals) {
            System.out.println(THIN);
            System.out.println(professional);
        }
        System.out.println(THIN);

        System.out.println();
        System.out.println("POLYMORPHISM - one loop, every object performs its own duty:");
        for (HealthProfessional professional : professionals) {
            System.out.println("  * " + professional.performPrimaryDuty(patient));
        }

        System.out.println();
        System.out.println("INTERFACE - a Prescriber exposes prescribing, the other types simply cannot:");
        for (HealthProfessional professional : professionals) {
            if (professional instanceof Prescriber) {
                Prescriber prescriber = (Prescriber) professional;
                System.out.println("  * " + prescriber.prescribeMedication(patient, "Amoxicillin 500 mg"));
            } else {
                System.out.println("  * " + professional.getName() + " (" + professional.getRoleDescription()
                        + ") may not prescribe medication.");
            }
        }

        System.out.println();
        System.out.println("IDENTITY - two objects with the same ID are the same person:");
        HealthProfessional amy = findById(professionals, 101);
        HealthProfessional secondAmy = new GeneralPractitioner(101, "Dr Amy",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), true);
        System.out.println("  * amy.equals(secondAmy)  -> " + amy.equals(secondAmy));
        System.out.println("  * same hashCode          -> " + (amy.hashCode() == secondAmy.hashCode()));
        System.out.println("  * the manager uses this exact rule to reject double bookings (part 5).");
    }

    /**
     * Demonstrates part 4: creating one appointment, printing it and sorting a
     * small list by time. It also shows that an illegal appointment cannot be
     * constructed at all.
     *
     * @param professionals staff of the clinic
     * @param tom           first patient
     * @param lily          second patient
     * @param jack          third patient
     */
    private static void demoAppointmentClass(List<HealthProfessional> professionals,
            Patient tom, Patient lily, Patient jack) {
        printHeader("PART 4 - THE APPOINTMENT OBJECT");

        HealthProfessional amy = findById(professionals, 101);
        HealthProfessional lisa = findById(professionals, 201);
        HealthProfessional sam = findById(professionals, 301);

        System.out.println("One appointment prints the booking and the full professional details:");
        Appointment sample = new Appointment(tom, amy, at("08:00"));
        System.out.println(sample);
        System.out.println("  The booking fills the slot " + sample.getTime() + "-"
                + ClinicSchedule.standard().getSlotEnd(sample.getTime()) + " of the standard timetable.");

        System.out.println();
        System.out.println("Appointments know their own order, so a collection can be sorted (Comparable):");
        List<Appointment> unsorted = new ArrayList<>();
        unsorted.add(new Appointment(lily, sam, at("11:30")));
        unsorted.add(new Appointment(jack, lisa, at("10:00")));
        unsorted.add(new Appointment(tom, amy, at("08:00")));
        System.out.println("  before sorting: " + times(unsorted));
        Collections.sort(unsorted);
        System.out.println("  after sorting : " + times(unsorted));
        System.out.println("  earliest booking is " + unsorted.get(0).getTime() + " with "
                + unsorted.get(0).getProfessional().getName() + ".");

        System.out.println();
        System.out.println("INVALID OBJECTS ARE IMPOSSIBLE - the constructor validates every argument:");
        showRejected("no patient", () -> new Appointment(null, amy, at("09:00")));
        showRejected("no professional", () -> new Appointment(tom, null, at("09:00")));
        showRejected("time before opening", () -> new Appointment(tom, amy, at("07:30")));
        showRejected("time between two slots", () -> new Appointment(tom, amy, at("09:15")));
        showRejected("time after closing", () -> new Appointment(tom, amy, at("16:00")));
    }

    /**
     * Demonstrates parts 5 and 6: the full life cycle of the appointment
     * collection with valid operations first and every failure case afterwards.
     *
     * @param professionals staff of the clinic
     * @param tom           first patient
     * @param lily          second patient
     * @param jack          third patient
     */
    private static void demoAppointmentManager(List<HealthProfessional> professionals,
            Patient tom, Patient lily, Patient jack) {
        printHeader("PART 5 + 6 - APPOINTMENT MANAGER, END TO END");

        HealthProfessional amy = findById(professionals, 101);
        HealthProfessional bob = findById(professionals, 102);
        HealthProfessional lisa = findById(professionals, 201);
        HealthProfessional sam = findById(professionals, 301);
        HealthProfessional zoe = findById(professionals, 401);

        AppointmentManager manager = new AppointmentManager();
        ClinicSchedule schedule = manager.getSchedule();
        System.out.println("Timetable      : " + schedule);
        System.out.println("Bookings are   : " + LocalDate.now() + " only");
        System.out.println("Slots per day  : " + schedule.getSlotCount() + ", from "
                + schedule.getSlots().get(0) + " to "
                + schedule.getSlots().get(schedule.getSlotCount() - 1) + ".");

        printSubHeader("ADDING APPOINTMENTS - different types of professional");
        printResult(manager.addAppointment(new Appointment(tom, amy, at("08:00"))));
        printResult(manager.addAppointment(new Appointment(lily, bob, at("09:30"))));
        printResult(manager.addAppointment(new Appointment(tom, sam, at("09:30"))));
        printResult(manager.addAppointment(new Appointment(jack, lisa, at("10:00"))));
        printResult(manager.addAppointment(new Appointment(tom, sam, at("11:30"))));
        printResult(manager.addAppointment(new Appointment(lily, zoe, at("13:00"))));
        printResult(manager.addAppointment(new Appointment(jack, amy, at("14:30"))));

        printTable("ALL APPOINTMENTS - earliest first", manager.getAllAppointments());
        System.out.println("  09:30 is booked with two different professionals, which is allowed. The");
        System.out.println("  same professional twice at one time is rejected (see FAILURES below).");

        printSubHeader("VIEW AND FILTER");
        printTable("Filter by professional ID 101 (Dr Amy)", manager.filterByProfessionalId(101));
        printTable("Filter by professional object Dr Amy (equals by ID)", manager.filterByProfessional(amy));
        printTable("Filter by patient mobile 0400 222 222 (Lily)", manager.filterByPatientPhone("0400 222 222"));
        printTable("Filter by patient object Jack", manager.filterByPatient(jack));
        printTable("Filter by unknown professional ID 999", manager.filterByProfessionalId(999));

        List<Appointment> amyBookings = manager.filterByProfessionalId(101);
        Collections.sort(amyBookings, Appointment.BY_PATIENT_NAME);
        printTable("The same subset re-sorted by patient name (Comparator)", amyBookings);
        printTable("All appointments ordered by patient name (Comparator)",
                manager.getAllAppointments(Appointment.BY_PATIENT_NAME));

        printSubHeader("FREE SLOTS");
        List<LocalTime> freeSlots = manager.getAvailableSlots(amy);
        System.out.println("  Dr Amy still has " + freeSlots.size() + " free slots: " + freeSlots);

        printSubHeader("FAILURES - every refused operation returns a warning message");
        printResult(manager.addAppointment(new Appointment(lily, amy, at("08:00"))));
        System.out.println("  The next booking uses a second object with ID 101 - the manager still");
        System.out.println("  sees the same person, because equality is based on the ID:");
        HealthProfessional secondAmy = new GeneralPractitioner(101, "Dr Amy",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), true);
        printResult(manager.addAppointment(new Appointment(tom, secondAmy, at("08:00"))));
        printResult(manager.addAppointment(new Appointment(tom, amy, LocalDate.now().plusDays(1), at("09:00"))));
        printResult(manager.addAppointment(null));
        printResult(manager.cancelAppointment(999, at("14:00")));
        printResult(manager.cancelAppointment(101, at("15:30")));
        printResult(manager.cancelAppointmentsForPatient("0499999999"));
        printResult(manager.cancelAppointmentsForPatient("   "));

        printSubHeader("CANCEL ONE APPOINTMENT BY PROFESSIONAL ID + TIME");
        printResult(manager.cancelAppointment(101, at("08:00")));
        printTable("APPOINTMENTS AFTER THE CANCELLATION", manager.getAllAppointments());

        printSubHeader("CANCEL EVERY APPOINTMENT OF ONE PATIENT BY MOBILE NUMBER");
        printResult(manager.cancelAppointmentsForPatient("0400 333 333"));
        printTable("FINAL APPOINTMENTS", manager.getAllAppointments());
        System.out.println("  Appointments left: " + manager.getAppointmentCount() + ".");

        System.out.println();
        System.out.println("  An earlier cancelled slot can be booked again:");
        printResult(manager.addAppointment(new Appointment(lily, amy, at("08:00"))));
        printTable("AFTER RE-BOOKING THE RELEASED SLOT", manager.getAllAppointments());
    }

    /**
     * Demonstrates the design point of part 5: the timetable is an object, so a
     * different timetable can be plugged in without touching any other class.
     *
     * @param professionals staff of the clinic
     * @param tom           first patient
     * @param lily          second patient
     * @param jack          third patient
     */
    private static void demoScheduleReplacement(List<HealthProfessional> professionals,
            Patient tom, Patient lily, Patient jack) {
        printHeader("PART 5 DESIGN POINT - REPLACING THE TIMETABLE");

        HealthProfessional amy = findById(professionals, 101);
        ClinicSchedule hourly = new ClinicSchedule(LocalTime.of(9, 0), LocalTime.of(12, 0), 60);
        AppointmentManager manager = new AppointmentManager(hourly);

        System.out.println("New timetable  : " + manager.getSchedule());
        System.out.println("  Nothing else changed: the manager and the appointments simply ask");
        System.out.println("  the new schedule which slots exist.");

        printResult(manager.addAppointment(new Appointment(tom, amy, at("09:00"), hourly)));
        printResult(manager.addAppointment(new Appointment(lily, amy, at("10:00"), hourly)));
        printResult(manager.addAppointment(new Appointment(jack, amy, at("09:00"), hourly)));

        System.out.println();
        System.out.println("  A time that is valid in the 30 minute timetable is not valid here:");
        printResult(manager.addAppointment(new Appointment(jack, amy, at("10:30"))));
        showRejected("constructing against the hourly timetable",
                () -> new Appointment(jack, amy, at("10:30"), hourly));

        printTable("APPOINTMENTS IN THE HOURLY TIMETABLE", manager.getAllAppointments());
    }

    /**
     * Prints a rejected constructor call in a readable way.
     *
     * @param label  what was attempted
     * @param action constructor call that is expected to fail
     */
    private static void showRejected(String label, Runnable action) {
        try {
            action.run();
            System.out.println("  [NOT REJECTED] " + label);
        } catch (IllegalArgumentException exception) {
            System.out.println("  [REJECTED] " + label + " -> " + exception.getMessage());
        }
    }

    /**
     * Looks a professional up by ID.
     *
     * @param professionals staff of the clinic
     * @param id            wanted ID
     * @return the matching professional
     * @throws IllegalArgumentException if no professional has that ID
     */
    private static HealthProfessional findById(List<HealthProfessional> professionals, int id) {
        for (HealthProfessional professional : professionals) {
            if (professional.getId() == id) {
                return professional;
            }
        }
        throw new IllegalArgumentException("No health professional with ID " + id + ".");
    }

    /**
     * Shortcut for building a slot time from "HH:mm".
     *
     * @param time time in 24 hour format, for example "13:30"
     * @return the parsed time
     */
    private static LocalTime at(String time) {
        return LocalTime.parse(time);
    }

    /**
     * Returns the start times of the given appointments in list order.
     *
     * @param appointments appointments to inspect
     * @return their start times in the current list order
     */
    private static List<LocalTime> times(List<Appointment> appointments) {
        List<LocalTime> times = new ArrayList<>();
        for (Appointment appointment : appointments) {
            times.add(appointment.getTime());
        }
        return times;
    }

    /**
     * Prints a section title surrounded by rules.
     *
     * @param title title of the section
     */
    private static void printHeader(String title) {
        System.out.println();
        System.out.println(RULE);
        System.out.println(title);
        System.out.println(RULE);
    }

    /**
     * Prints a subtitle followed by a thin rule.
     *
     * @param title title of the sub-section
     */
    private static void printSubHeader(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(THIN);
    }

    /**
     * Prints one operation result.
     *
     * @param result result returned by the manager
     */
    private static void printResult(OperationResult result) {
        System.out.println("  " + result);
    }

    /**
     * Prints appointments as a small table: time, professional, patient.
     *
     * @param title        caption of the table
     * @param appointments appointments to print, already in the wanted order
     */
    private static void printTable(String title, List<Appointment> appointments) {
        printSubHeader(title + " [" + appointments.size() + "]");
        if (appointments.isEmpty()) {
            System.out.println("  (no appointments match)");
            return;
        }
        System.out.printf("  %-5s  %-46s  %s%n", "TIME", "HEALTH PROFESSIONAL", "PATIENT");
        for (Appointment appointment : appointments) {
            System.out.printf("  %-5s  %-46s  %s%n",
                    appointment.getTime(),
                    appointment.getProfessional().summary(),
                    appointment.getPatient());
        }
    }
}
