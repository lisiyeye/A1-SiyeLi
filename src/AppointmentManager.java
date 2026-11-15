import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Keeps the appointment collection of the clinic and offers the operations
 * required by part 5 of the brief: add, view/filter, sort, cancel - and a
 * warning message whenever an operation cannot be performed.
 *
 * <p><b>Choice of collections.</b> The appointments themselves are kept in an
 * {@link ArrayList}: the set is small (one day, a handful of staff), insertion
 * order is kept for nothing but clarity, and a list can be sorted and printed in
 * any order asked for. To reject a double booking without scanning the whole
 * list, a second structure indexes the booked slots:
 * {@code Map<HealthProfessional, Set<LocalTime>>}. A {@link HashMap} finds the
 * professional in constant time and a {@link HashSet} answers "is this slot
 * taken" in constant time as well. The two structures are updated together, so
 * the index can never drift away from the list.</p>
 *
 * <p><b>Same professional.</b> The map is keyed by the
 * {@link HealthProfessional} object itself. Because the base class implements
 * {@link Object#equals(Object)} and {@link Object#hashCode()} using the ID, a
 * second object that represents the same person (same ID) lands in the same map
 * entry. That is exactly the rule the brief asks about in part 5: two
 * professionals are "the same" when their IDs are equal.</p>
 *
 * <p><b>Changeable timetable.</b> Every time check goes through
 * {@link #getSchedule()}, so a manager built with a different
 * {@link ClinicSchedule} accepts the new slots without any other code change.
 * The professional's weekly working days are stored and can be queried with
 * {@link HealthProfessional#worksOn(java.time.DayOfWeek)}, but they are not used
 * to block a booking: the constraints stated in part 5 are the timetable and the
 * double booking rule. Adding a roster check would be a single extra test in
 * {@link #addAppointment(Appointment)}.</p>
 */
public class AppointmentManager {

    /** Primary storage: every confirmed appointment. */
    private final List<Appointment> appointments;

    /** Conflict index: professional -> start times that are already taken. */
    private final Map<HealthProfessional, Set<LocalTime>> bookedSlots;

    /** Timetable of this clinic; all slot checks use this object. */
    private final ClinicSchedule schedule;

    /**
     * Creates a manager for the timetable required by the brief
     * (08:00-16:00, 30 minute slots).
     */
    public AppointmentManager() {
        this(ClinicSchedule.standard());
    }

    /**
     * Creates a manager for a custom timetable.
     *
     * @param schedule timetable of the clinic, not null
     * @throws IllegalArgumentException if the schedule is null
     */
    public AppointmentManager(ClinicSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("A clinic timetable is required.");
        }
        this.schedule = schedule;
        this.appointments = new ArrayList<>();
        this.bookedSlots = new HashMap<>();
    }

    /**
     * Returns the timetable used by this manager.
     *
     * @return the timetable used for all slot checks
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Adds an appointment after checking every rule of part 5.
     *
     * @param appointment appointment to add
     * @return success with a confirmation message, or a warning explaining why
     *         the appointment was rejected
     */
    public OperationResult addAppointment(Appointment appointment) {
        if (appointment == null) {
            return OperationResult.warning("No appointment was supplied, so nothing was added.");
        }
        if (!appointment.getDate().equals(LocalDate.now())) {
            return OperationResult.warning("Only same-day appointments are accepted; "
                    + appointment.getDate() + " is not today (" + LocalDate.now() + ").");
        }
        if (!schedule.isBookableSlot(appointment.getTime())) {
            return OperationResult.warning("Time " + appointment.getTime()
                    + " is not a bookable slot. This clinic works " + schedule + ".");
        }
        HealthProfessional professional = appointment.getProfessional();
        if (isBooked(professional, appointment.getTime())) {
            return OperationResult.warning("Duplicate booking rejected: " + professional.summary()
                    + " already has an appointment at " + appointment.getTime() + ".");
        }
        appointments.add(appointment);
        bookedSlots.computeIfAbsent(professional, key -> new HashSet<>()).add(appointment.getTime());
        return OperationResult.success("Appointment added: " + appointment.getPatient()
                + " with " + professional.getName() + " at " + appointment.getTime() + ".");
    }

    /**
     * Cancels one appointment, selected by professional ID and start time.
     *
     * @param professionalId ID of the selected professional
     * @param time           start time of the slot
     * @return success with a confirmation message, or a warning when no such
     *         appointment exists
     */
    public OperationResult cancelAppointment(int professionalId, LocalTime time) {
        if (professionalId <= 0) {
            return OperationResult.warning("A positive professional ID is required but was " + professionalId + ".");
        }
        if (time == null) {
            return OperationResult.warning("A start time is required to cancel an appointment.");
        }
        for (Iterator<Appointment> iterator = appointments.iterator(); iterator.hasNext();) {
            Appointment candidate = iterator.next();
            if (candidate.getProfessional().getId() == professionalId && candidate.getTime().equals(time)) {
                iterator.remove();
                release(candidate.getProfessional(), time);
                return OperationResult.success("Appointment cancelled: " + candidate.getPatient()
                        + " with " + candidate.getProfessional().getName() + " at " + time + ".");
            }
        }
        return OperationResult.warning("No appointment found for professional ID " + professionalId
                + " at " + time + ".");
    }

    /**
     * Cancels every appointment of one patient, selected by mobile number.
     *
     * @param mobilePhone patient mobile number
     * @return success listing the cancelled slots, or a warning when nothing matched
     */
    public OperationResult cancelAppointmentsForPatient(String mobilePhone) {
        String phone = normalizePhone(mobilePhone);
        if (phone.isEmpty()) {
            return OperationResult.warning("A patient mobile number is required to cancel appointments.");
        }
        List<Appointment> removed = new ArrayList<>();
        for (Iterator<Appointment> iterator = appointments.iterator(); iterator.hasNext();) {
            Appointment candidate = iterator.next();
            if (candidate.getPatient().getMobilePhone().equals(phone)) {
                iterator.remove();
                release(candidate.getProfessional(), candidate.getTime());
                removed.add(candidate);
            }
        }
        if (removed.isEmpty()) {
            return OperationResult.warning("No appointments found for patient mobile number "
                    + formatPhone(phone) + ".");
        }
        StringBuilder slots = new StringBuilder();
        for (Appointment appointment : removed) {
            if (slots.length() > 0) {
                slots.append("; ");
            }
            slots.append(appointment.getTime()).append(" with ")
                    .append(appointment.getProfessional().getName())
                    .append(" [ID ").append(appointment.getProfessional().getId()).append("]");
        }
        return OperationResult.success("Cancelled " + removed.size() + " appointment(s) for mobile number "
                + formatPhone(phone) + ": " + slots + ".");
    }

    /**
     * Returns every appointment, sorted from earliest to latest.
     *
     * @return sorted copy of all appointments
     */
    public List<Appointment> getAllAppointments() {
        return sortedCopy(appointments);
    }

    /**
     * Returns every appointment in the order of the given comparator. The demo
     * uses it to show that a collection can be ordered differently from its
     * natural order.
     *
     * @param order comparator describing the wanted order, not null
     * @return sorted copy of all appointments
     * @throws IllegalArgumentException if the comparator is null
     */
    public List<Appointment> getAllAppointments(Comparator<Appointment> order) {
        if (order == null) {
            throw new IllegalArgumentException("A comparator is required to sort appointments.");
        }
        List<Appointment> copy = new ArrayList<>(appointments);
        Collections.sort(copy, order);
        return copy;
    }

    /**
     * Filters by professional ID.
     *
     * @param professionalId ID of the professional
     * @return matching appointments, earliest first
     */
    public List<Appointment> filterByProfessionalId(int professionalId) {
        return filterBy(appointment -> appointment.getProfessional().getId() == professionalId);
    }

    /**
     * Filters by professional object. Uses {@link HealthProfessional#equals(Object)},
     * so any object with the same ID selects the same person.
     *
     * @param professional professional to look for, not null
     * @return matching appointments, earliest first
     * @throws IllegalArgumentException if the professional is null
     */
    public List<Appointment> filterByProfessional(HealthProfessional professional) {
        if (professional == null) {
            throw new IllegalArgumentException("A health professional is required for this filter.");
        }
        return filterBy(appointment -> appointment.getProfessional().equals(professional));
    }

    /**
     * Filters by patient mobile number.
     *
     * @param mobilePhone patient mobile number; spaces are ignored
     * @return matching appointments, earliest first
     */
    public List<Appointment> filterByPatientPhone(String mobilePhone) {
        String phone = normalizePhone(mobilePhone);
        if (phone.isEmpty()) {
            return new ArrayList<>();
        }
        return filterBy(appointment -> appointment.getPatient().getMobilePhone().equals(phone));
    }

    /**
     * Filters by patient object, using the phone number as identity.
     *
     * @param patient patient to look for, not null
     * @return matching appointments, earliest first
     * @throws IllegalArgumentException if the patient is null
     */
    public List<Appointment> filterByPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("A patient is required for this filter.");
        }
        return filterBy(appointment -> appointment.getPatient().equals(patient));
    }

    /**
     * Returns the number of stored appointments.
     *
     * @return number of stored appointments
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    /**
     * Returns the slots of the timetable that are still free for a professional.
     *
     * @param professional professional whose day is inspected, not null
     * @return free start times in chronological order
     * @throws IllegalArgumentException if the professional is null
     */
    public List<LocalTime> getAvailableSlots(HealthProfessional professional) {
        if (professional == null) {
            throw new IllegalArgumentException("A health professional is required to look up free slots.");
        }
        List<LocalTime> free = new ArrayList<>();
        for (LocalTime slot : schedule.getSlots()) {
            if (!isBooked(professional, slot)) {
                free.add(slot);
            }
        }
        return free;
    }

    /**
     * Checks the conflict index. Two professionals with the same ID are the same
     * key in the map, so the check also catches a booking made with a second
     * object that represents the same person.
     *
     * @param professional professional to check
     * @param time         start time to check
     * @return true when that professional already has an appointment then
     */
    private boolean isBooked(HealthProfessional professional, LocalTime time) {
        Set<LocalTime> times = bookedSlots.get(professional);
        return times != null && times.contains(time);
    }

    /**
     * Removes one slot from the conflict index.
     *
     * @param professional professional whose slot is released
     * @param time         start time that becomes free again
     */
    private void release(HealthProfessional professional, LocalTime time) {
        Set<LocalTime> times = bookedSlots.get(professional);
        if (times != null) {
            times.remove(time);
            if (times.isEmpty()) {
                bookedSlots.remove(professional);
            }
        }
    }

    /**
     * Applies a condition to every appointment and returns the matches in
     * chronological order.
     *
     * @param condition test an appointment has to pass
     * @return sorted list of matching appointments
     */
    private List<Appointment> filterBy(Predicate<Appointment> condition) {
        List<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (condition.test(appointment)) {
                matches.add(appointment);
            }
        }
        Collections.sort(matches);
        return matches;
    }

    /**
     * Copies a list of appointments and sorts it by time.
     *
     * @param source appointments to copy and sort
     * @return new list sorted with the natural order of {@link Appointment}
     */
    private static List<Appointment> sortedCopy(List<Appointment> source) {
        List<Appointment> copy = new ArrayList<>(source);
        Collections.sort(copy);
        return copy;
    }

    /**
     * Removes spaces from a phone number so "0400 111 111" and "0400111111"
     * select the same patient.
     *
     * @param mobilePhone number supplied by the caller, may be null
     * @return number without spaces, or an empty string when nothing was supplied
     */
    private static String normalizePhone(String mobilePhone) {
        return mobilePhone == null ? "" : mobilePhone.replace(" ", "").trim();
    }

    /**
     * Groups a ten digit number for reading, so messages show "0400 333 333".
     *
     * @param phone normalized phone number
     * @return grouped number, or the argument unchanged when it has another length
     */
    private static String formatPhone(String phone) {
        if (phone.length() != 10) {
            return phone;
        }
        return phone.substring(0, 4) + " " + phone.substring(4, 7) + " " + phone.substring(7);
    }
}
