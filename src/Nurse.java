import java.util.ArrayList;

/**
 * Nurse subclass of HealthProfessional
 */
public class Nurse extends HealthProfessional {
    private final String certificationLevel;

    public Nurse(int id, String name, ArrayList<String> availableDays, String certificationLevel) {
        super(id, name, availableDays);
        this.certificationLevel = certificationLevel;
    }

    public String getCertificationLevel() {
        return certificationLevel;
    }

    @Override
    public String getRoleDescription() {
        return "Registered Nurse";
    }

    @Override
    public String toString() {
        return super.toString() + "\nCertification level: " + certificationLevel;
    }
}
