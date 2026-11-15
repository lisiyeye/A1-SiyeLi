import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The clinic timetable: opening time, closing time and the length of one
 * appointment slot.
 *
 * <p>The brief fixes the timetable at 08:00-16:00 with a slot every 30 minutes,
 * but it also asks for a timetable that can be changed without touching the
 * rest of the system. That is why the timetable is an object instead of a bunch
 * of constants: the {@link AppointmentManager} asks its schedule which slots
 * exist, so replacing the schedule changes every check at once.</p>
 *
 * <p>The closing time is the end of the last slot, so the last bookable start
 * time is one slot before closing (15:30 in the standard timetable).</p>
 */
public final class ClinicSchedule {

    /** The clinic timetable required by the brief: 08:00-16:00, 30 minute slots. */
    private static final ClinicSchedule STANDARD =
            new ClinicSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0), 30);

    /** First bookable start time of the day. */
    private final LocalTime openingTime;

    /** End of the last slot of the day. */
    private final LocalTime closingTime;

    /** Length of one appointment slot in minutes. */
    private final int slotMinutes;

    /** All bookable start times, generated once because the object is immutable. */
    private final List<LocalTime> slots;

    /**
     * Creates a timetable.
     *
     * @param openingTime first bookable start time, not null
     * @param closingTime end of the last slot, must be after the opening time
     * @param slotMinutes slot length in minutes, must be positive and must divide
     *                    the working day exactly
     * @throws IllegalArgumentException if the timetable is inconsistent
     */
    public ClinicSchedule(LocalTime openingTime, LocalTime closingTime, int slotMinutes) {
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("Opening and closing times must not be null.");
        }
        if (slotMinutes <= 0) {
            throw new IllegalArgumentException("Slot length must be a positive number of minutes.");
        }
        LocalTime opening = openingTime.withSecond(0).withNano(0);
        LocalTime closing = closingTime.withSecond(0).withNano(0);
        if (!opening.isBefore(closing)) {
            throw new IllegalArgumentException("Opening time must be before closing time.");
        }
        long workingMinutes = Duration.between(opening, closing).toMinutes();
        if (workingMinutes % slotMinutes != 0) {
            throw new IllegalArgumentException("Slot length " + slotMinutes
                    + " minutes does not divide the working day (" + workingMinutes + " minutes) exactly.");
        }
        this.openingTime = opening;
        this.closingTime = closing;
        this.slotMinutes = slotMinutes;
        this.slots = Collections.unmodifiableList(buildSlots());
    }

    /**
     * Returns the standard timetable required by the brief.
     *
     * @return the clinic timetable used by the brief (08:00-16:00, 30 minutes)
     */
    public static ClinicSchedule standard() {
        return STANDARD;
    }

    /**
     * Builds every bookable start time of the day.
     *
     * @return start times from the opening time up to, but not including, closing
     */
    private List<LocalTime> buildSlots() {
        long slotCount = Duration.between(openingTime, closingTime).toMinutes() / slotMinutes;
        List<LocalTime> generated = new ArrayList<>();
        for (long index = 0; index < slotCount; index++) {
            generated.add(openingTime.plusMinutes(index * slotMinutes));
        }
        return generated;
    }

    /**
     * Returns the first bookable start time of the day.
     *
     * @return first bookable start time
     */
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    /**
     * Returns the end of the last slot of the day.
     *
     * @return end of the last slot
     */
    public LocalTime getClosingTime() {
        return closingTime;
    }

    /**
     * Returns the length of one slot in minutes.
     *
     * @return slot length in minutes
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * Returns every bookable start time of the day.
     *
     * @return every bookable start time as an unmodifiable list
     */
    public List<LocalTime> getSlots() {
        return slots;
    }

    /**
     * Returns how many slots one day has.
     *
     * @return number of bookable slots per day
     */
    public int getSlotCount() {
        return slots.size();
    }

    /**
     * Tests whether an appointment may start at the given time.
     *
     * <p>The check is exact membership of the generated slot list, so seconds
     * and nanoseconds are rejected as well: a booking starts on a slot boundary
     * or it does not exist.</p>
     *
     * @param time candidate start time, may be null
     * @return true when the time is a bookable slot of this timetable
     */
    public boolean isBookableSlot(LocalTime time) {
        return time != null && time.getSecond() == 0 && time.getNano() == 0 && slots.contains(time);
    }

    /**
     * Returns the end time of the given slot.
     *
     * @param start start time of a slot
     * @return end time of that slot, or null when the start time is not bookable
     */
    public LocalTime getSlotEnd(LocalTime start) {
        return isBookableSlot(start) ? start.plusMinutes(slotMinutes) : null;
    }

    /**
     * Returns a readable summary of the timetable.
     *
     * @return readable timetable, for example "08:00-16:00 (30 min slots, 16 per day)"
     */
    @Override
    public String toString() {
        return openingTime + "-" + closingTime + " (" + slotMinutes + " min slots, " + slots.size() + " per day)";
    }
}
