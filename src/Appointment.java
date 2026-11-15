import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * One same-day appointment: which patient, which health professional and at what
 * time.
 *
 * <p>Design decisions (part 4 of the brief):</p>
 * <ul>
 *   <li><b>No incomplete state.</b> All four values are private and final and
 *       are validated in the constructor. A null patient, a null professional, a
 *       missing date/time or a time that is not a real slot of the clinic
 *       timetable is rejected with an {@link IllegalArgumentException}, so an
 *       {@code Appointment} object is always usable.</li>
 *   <li><b>Time is stored as {@link LocalTime}, not as text.</b> Sorting text
 *       works only while every string has the same shape; {@code LocalTime}
 *       compares by real clock order and therefore stays correct for any
 *       timetable, including one with slots such as 09:45.</li>
 *   <li><b>The object knows its own order.</b> {@link #compareTo(Appointment)}
 *       orders by date, then start time, then professional ID and finally
 *       patient phone number, so a whole collection can be sorted from earliest
 *       to latest and two equal-time bookings still have a stable order. The
 *       brief allows any order for the same time, a deterministic one is simply
 *       nicer to read.</li>
 *   <li><b>Any type of professional can be booked.</b> The field type is the
 *       abstract base class, so appointments do not care whether the selected
 *       person is a GP, a nurse, a surgeon or a dietitian.</li>
 * </ul>
 */
public final class Appointment implements Comparable<Appointment> {

    /**
     * Alternative order used to show that a collection can be sorted in more
     * than one way: patient name first, then time. It is a {@link Comparator}
     * that lives outside the natural order defined by {@link #compareTo}.
     */
    public static final Comparator<Appointment> BY_PATIENT_NAME =
            Comparator.comparing((Appointment appointment) -> appointment.patient.getName(),
                    String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Appointment::getTime)
                    .thenComparingInt(appointment -> appointment.professional.getId());

    /** Day of the visit. The clinic only takes bookings for today. */
    private final LocalDate date;

    /** Start time of the slot. */
    private final LocalTime time;

    /** Patient who is booked in. */
    private final Patient patient;

    /** Professional the patient is booked with; any concrete type is allowed. */
    private final HealthProfessional professional;

    /**
     * Creates an appointment for today, validated against the standard clinic
     * timetable. This is the constructor the demo uses most of the time.
     *
     * @param patient      patient who is booked in, not null
     * @param professional professional the patient sees, not null
     * @param time         start time of the slot, must be a bookable slot today
     * @throws IllegalArgumentException if any value is missing or the time is not
     *                                  a slot of the standard timetable
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        this(patient, professional, LocalDate.now(), time);
    }

    /**
     * Creates an appointment for today, validated against a caller supplied
     * timetable.
     *
     * @param patient      patient who is booked in, not null
     * @param professional professional the patient sees, not null
     * @param time         start time of the slot, must be bookable in the schedule
     * @param schedule     timetable the time is checked against, not null
     * @throws IllegalArgumentException if any value is missing or the time is not
     *                                  a slot of the given timetable
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time, ClinicSchedule schedule) {
        this(patient, professional, LocalDate.now(), time, schedule);
    }

    /**
     * Creates an appointment for a given day, validated against the standard
     * clinic timetable.
     *
     * @param patient      patient who is booked in, not null
     * @param professional professional the patient sees, not null
     * @param date         day of the visit, not null
     * @param time         start time of the slot, must be a bookable slot
     * @throws IllegalArgumentException if any value is missing or the time is not
     *                                  a slot of the standard timetable
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalDate date, LocalTime time) {
        this(patient, professional, date, time, ClinicSchedule.standard());
    }

    /**
     * Creates an appointment validated against a caller supplied timetable. This
     * overload is what makes a replaced timetable usable: the same rules are
     * checked against the new slots.
     *
     * @param patient      patient who is booked in, not null
     * @param professional professional the patient sees, not null
     * @param date         day of the visit, not null
     * @param time         start time of the slot, must be bookable in the schedule
     * @param schedule     timetable the time is checked against, not null
     * @throws IllegalArgumentException if any value is missing or the time is not
     *                                  a slot of the given timetable
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalDate date, LocalTime time,
            ClinicSchedule schedule) {
        if (patient == null) {
            throw new IllegalArgumentException("An appointment needs a patient.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment needs a health professional.");
        }
        if (date == null) {
            throw new IllegalArgumentException("An appointment needs a date.");
        }
        if (time == null) {
            throw new IllegalArgumentException("An appointment needs a start time.");
        }
        if (schedule == null) {
            throw new IllegalArgumentException("An appointment needs a timetable to validate the time.");
        }
        if (!schedule.isBookableSlot(time)) {
            throw new IllegalArgumentException("Time " + time + " is not a bookable slot of " + schedule + ".");
        }
        this.patient = patient;
        this.professional = professional;
        this.date = date;
        this.time = time;
    }

    /**
     * Returns the day the appointment is booked for.
     *
     * @return day of the visit
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the start time of the slot.
     *
     * @return start time of the slot
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns the patient who is booked in.
     *
     * @return patient who is booked in
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the selected health professional.
     *
     * @return selected health professional, as the shared base type
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Orders appointments from earliest to latest. Two appointments that start at
     * the same moment are ordered by professional ID and then by patient phone,
     * which keeps the output stable without contradicting the brief.
     *
     * @param other appointment to compare with
     * @return negative, zero or positive according to the chronological order
     */
    @Override
    public int compareTo(Appointment other) {
        int byDate = date.compareTo(other.date);
        if (byDate != 0) {
            return byDate;
        }
        int byTime = time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        int byProfessional = Integer.compare(professional.getId(), other.professional.getId());
        if (byProfessional != 0) {
            return byProfessional;
        }
        return patient.getMobilePhone().compareTo(other.patient.getMobilePhone());
    }

    /**
     * Two appointments are equal when they describe the same slot for the same
     * patient and professional. This agrees with {@link #compareTo(Appointment)},
     * which matters for sorted collections.
     *
     * @param other object to compare with
     * @return true when all four values match
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Appointment)) {
            return false;
        }
        Appointment that = (Appointment) other;
        return date.equals(that.date)
                && time.equals(that.time)
                && professional.equals(that.professional)
                && patient.equals(that.patient);
    }

    /**
     * @return hash code built from the four values, consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, time, professional, patient);
    }

    /**
     * Prints the booking together with the full details of the chosen
     * professional, as required by part 4.
     *
     * @return multi-line description of the appointment
     */
    @Override
    public String toString() {
        return "[Appointment] " + date + " " + time
                + "\n  Patient      : " + patient
                + "\n  Professional :" + indent(professional.toString(), "    ");
    }

    /**
     * Prefixes every line of a text block with the given indentation.
     *
     * @param text   text to indent
     * @param prefix prefix added in front of every line
     * @return the indented text, starting with a line separator
     */
    private static String indent(String text, String prefix) {
        StringBuilder indented = new StringBuilder();
        for (String line : text.split("\n")) {
            indented.append('\n').append(prefix).append(line);
        }
        return indented.toString();
    }
}
