import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Abstract superclass of every health professional who works in the clinic.
 *
 * <p>It owns the information that every professional has in common: a numeric
 * ID, a name and the fixed set of days the person works each week. Concrete
 * subclasses add their own data and their own behaviour.</p>
 *
 * <p>Design decisions (the answers to the questions in parts 1 and 2 of the
 * brief):</p>
 * <ul>
 *   <li><b>Encapsulation / no illegal state.</b> Every field is private and
 *       final. Arguments are validated in the constructor, the working days are
 *       copied into an {@link EnumSet} and handed out as an unmodifiable
 *       {@link Set}, so outside code can read the data but can never make the
 *       object invalid nor change it behind our back.</li>
 *   <li><b>Abstraction.</b> The class is {@code abstract}, so it can never be
 *       instantiated directly. The abstract methods force every concrete type
 *       to state what it is ({@link #getRoleDescription()}), to print its own
 *       details ({@link #describeAdditionalDetails()}) and to perform its own
 *       duty ({@link #performPrimaryDuty(Patient)}).</li>
 *   <li><b>Inheritance.</b> Subclasses only implement what differs; validation,
 *       roster handling and the layout of {@link #toString()} are written here
 *       once instead of once per type.</li>
 *   <li><b>Identity.</b> Two professionals are the same person when their IDs
 *       are equal, therefore {@link #equals(Object)} and {@link #hashCode()} use
 *       the ID only. That is also the rule the appointment manager relies on
 *       when it rejects a double booking (part 5 of the brief).</li>
 * </ul>
 */
public abstract class HealthProfessional {

    /**
     * Weekly roster used when a subclass constructor is called without an
     * explicit set of days: the normal Monday-to-Friday working week.
     */
    private static final Set<DayOfWeek> DEFAULT_WORKING_DAYS =
            Collections.unmodifiableSet(EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

    /** Column layout shared by {@link #toString()} and all detail lines. */
    private static final String DETAIL_FORMAT = "%-13s: %s";

    /** Numeric identifier, unique inside the clinic (digits only, positive). */
    private final int id;

    /** Name shown to patients and staff. */
    private final String name;

    /** Fixed weekly roster; immutable after construction. */
    private final Set<DayOfWeek> workingDays;

    /**
     * Creates a professional with an explicit weekly roster.
     *
     * @param id          numeric identifier, must be greater than zero
     * @param name        name of the professional, must not be blank
     * @param workingDays days the professional works each week, must not be empty
     * @throws IllegalArgumentException if any argument is invalid
     */
    protected HealthProfessional(int id, String name, Set<DayOfWeek> workingDays) {
        this.id = requirePositiveId(id);
        this.name = requireName(name);
        this.workingDays = copyOfWorkingDays(workingDays);
    }

    /**
     * Creates a professional who works the default Monday-to-Friday week.
     *
     * <p>This overload is the "reasonable default" asked for in part 1: a caller
     * that does not care about the roster does not have to build one.</p>
     *
     * @param id   numeric identifier, must be greater than zero
     * @param name name of the professional, must not be blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    protected HealthProfessional(int id, String name) {
        this(id, name, DEFAULT_WORKING_DAYS);
    }

    /**
     * Checks that the ID is a positive whole number.
     *
     * @param id candidate identifier
     * @return the same identifier, so it can be used in an assignment
     * @throws IllegalArgumentException if the ID is zero or negative
     */
    private static int requirePositiveId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Health professional ID must be a positive whole number (digits only) but was " + id + ".");
        }
        return id;
    }

    /**
     * Trims and validates a professional name.
     *
     * @param name candidate name
     * @return the trimmed name
     * @throws IllegalArgumentException if the name is null or blank
     */
    private static String requireName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Health professional name must not be blank.");
        }
        return name.trim();
    }

    /**
     * Makes a defensive, unmodifiable copy of the roster.
     *
     * @param workingDays roster supplied by the caller
     * @return unmodifiable copy of the roster
     * @throws IllegalArgumentException if the roster is null, empty or contains null
     */
    private static Set<DayOfWeek> copyOfWorkingDays(Set<DayOfWeek> workingDays) {
        if (workingDays == null || workingDays.isEmpty()) {
            throw new IllegalArgumentException("A health professional must work at least one day per week.");
        }
        for (DayOfWeek day : workingDays) {
            if (day == null) {
                throw new IllegalArgumentException("Working days must not contain null.");
            }
        }
        // EnumSet is the natural collection for a fixed set of enum constants:
        // compact, fast and it keeps the constants in their declared order.
        return Collections.unmodifiableSet(EnumSet.copyOf(workingDays));
    }

    /**
     * Returns the numeric identifier of this professional.
     *
     * @return the numeric identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the display name of this professional.
     *
     * @return the display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the weekly roster. The returned set is unmodifiable, so external
     * code can read the working days but cannot alter them.
     *
     * @return fixed weekly working days
     */
    public Set<DayOfWeek> getWorkingDays() {
        return workingDays;
    }

    /**
     * Tests whether this professional is rostered on the given day.
     *
     * @param day day to test, may be null
     * @return true when this professional is rostered on that day
     */
    public boolean worksOn(DayOfWeek day) {
        return day != null && workingDays.contains(day);
    }

    /**
     * Tests whether this professional is rostered today.
     *
     * @return true when this professional is rostered today
     */
    public boolean worksToday() {
        return worksOn(LocalDate.now().getDayOfWeek());
    }

    /**
     * Every concrete type must say what it is, so that a variable declared as
     * {@code HealthProfessional} can still tell the user which type it holds.
     *
     * @return short label of the concrete role, e.g. "General Practitioner (GP)"
     */
    public abstract String getRoleDescription();

    /**
     * Builds the extra detail lines of the concrete type. It is called by
     * {@link #toString()} and is the only formatting code a subclass writes.
     *
     * @return one or more lines, each starting with a line separator
     */
    protected abstract String describeAdditionalDetails();

    /**
     * Performs the main duty of this concrete type for the given patient.
     *
     * <p>This is the behaviour that makes polymorphism visible: one loop over
     * {@code HealthProfessional} objects, and every object reacts in its own
     * way.</p>
     *
     * @param patient patient who is being seen
     * @return readable description of what this professional did
     * @throws IllegalArgumentException if the patient is null
     */
    public abstract String performPrimaryDuty(Patient patient);

    /**
     * Returns a one-line summary for tables and messages.
     *
     * @return one-line summary, e.g. "Dr Amy - General Practitioner (GP) [ID 101]"
     */
    public String summary() {
        return name + " - " + getRoleDescription() + " [ID " + id + "]";
    }

    /**
     * Prints every stored value: the shared data from this class plus the
     * details contributed by the concrete type.
     *
     * <p>The method is final on purpose: subclasses must not be able to break
     * the common layout, they only fill in their own lines through
     * {@link #describeAdditionalDetails()}.</p>
     *
     * @return multi-line description of this professional
     */
    @Override
    public final String toString() {
        return detailLine("ID", id)
                + "\n" + detailLine("Name", name)
                + "\n" + detailLine("Role", getRoleDescription())
                + "\n" + detailLine("Working days", formatWorkingDays())
                + describeAdditionalDetails();
    }

    /**
     * Two health professionals represent the same person when their IDs match.
     * The concrete class is deliberately ignored: IDs are unique inside the
     * clinic, so an ID can never point at two different people.
     *
     * @param other object to compare with
     * @return true when the other object is a professional with the same ID
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        return id == ((HealthProfessional) other).id;
    }

    /**
     * @return hash code derived from the ID, consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Formats one "label : value" line with the shared column layout.
     *
     * @param label short field name
     * @param value value to display
     * @return formatted line without a trailing line separator
     */
    protected static String detailLine(String label, Object value) {
        return String.format(DETAIL_FORMAT, label, value);
    }

    /**
     * Guards subclass behaviour against a missing patient.
     *
     * @param patient patient supplied by the caller
     * @return the same patient, so it can be used in an assignment
     * @throws IllegalArgumentException if the patient is null
     */
    protected static Patient requirePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient must not be null.");
        }
        return patient;
    }

    /**
     * Formats the roster in calendar order, for example "Monday, Wednesday".
     *
     * @return readable working days
     */
    private String formatWorkingDays() {
        StringBuilder text = new StringBuilder();
        for (DayOfWeek day : DayOfWeek.values()) {
            if (workingDays.contains(day)) {
                if (text.length() > 0) {
                    text.append(", ");
                }
                text.append(prettyName(day));
            }
        }
        return text.toString();
    }

    /**
     * Turns an enum constant such as {@code WEDNESDAY} into "Wednesday".
     *
     * @param day day to prettify
     * @return capitalised day name
     */
    private static String prettyName(DayOfWeek day) {
        return day.name().charAt(0) + day.name().substring(1).toLowerCase(Locale.ROOT);
    }
}
