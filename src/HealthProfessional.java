import java.util.ArrayList;
import java.util.Objects;

/**
 * Abstract base class for all types of health professionals.
 * Cannot be instantiated directly. Stores shared data: id, name, available working days.
 */
public abstract class HealthProfessional {
    private final int id;
    private final String name;
    private final ArrayList<String> availableDays;

    /**
     * Construct a health professional object.
     * @param id numeric unique identifier
     * @param name name of professional
     * @param availableDays list of working days each week
     */
    public HealthProfessional(int id, String name, ArrayList<String> availableDays) {
        this.id = id;
        this.name = name;
        this.availableDays = availableDays;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getAvailableDays() {
        return availableDays;
    }

    /**
     * Abstract method returns role/type description, implemented by subclasses.
     * @return string description of professional role
     */
    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return "ID: " + id +
                "\nName: " + name +
                "\nRole: " + getRoleDescription() +
                "\nWorking days: " + availableDays;
    }

    /**
     * Two health professionals are equal if they share same id.
     * @param o object to compare
     * @return true if id matches
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthProfessional that = (HealthProfessional) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
