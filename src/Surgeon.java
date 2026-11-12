import java.util.ArrayList;

/**
 * Surgeon subclass of HealthProfessional
 */
public class Surgeon extends HealthProfessional {
    private final String surgerySpecialty;

    public Surgeon(int id, String name, ArrayList<String> availableDays, String surgerySpecialty) {
        super(id, name, availableDays);
        this.surgerySpecialty = surgerySpecialty;
    }

    public String getSurgerySpecialty() {
        return surgerySpecialty;
    }

    @Override
    public String getRoleDescription() {
        return "Surgeon";
    }

    @Override
    public String toString() {
        return super.toString() + "\nSurgery specialty: " + surgerySpecialty;
    }
}
