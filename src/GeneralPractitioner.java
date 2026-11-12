import java.util.ArrayList;

/**
 * General Practitioner subclass of HealthProfessional.
 * Has unique field canPrescribeMedication.
 */
public class GeneralPractitioner extends HealthProfessional {
    private final boolean canPrescribeMedication;

    /**
     * @param id unique id
     * @param name gp name
     * @param availableDays weekly working days
     * @param canPrescribeMedication whether able to prescribe medication
     */
    public GeneralPractitioner(int id, String name, ArrayList<String> availableDays, boolean canPrescribeMedication) {
        super(id, name, availableDays);
        this.canPrescribeMedication = canPrescribeMedication;
    }

    public boolean isCanPrescribeMedication() {
        return canPrescribeMedication;
    }

    @Override
    public String getRoleDescription() {
        return "General Practitioner (GP)";
    }

    @Override
    public String toString() {
        return super.toString() + "\nCan prescribe medication: " + canPrescribeMedication;
    }
}
