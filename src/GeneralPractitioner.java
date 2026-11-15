import java.time.DayOfWeek;
import java.util.Set;

/**
 * A general practitioner (GP): the clinic's first point of contact for a
 * same-day visit. A GP consults patients and, when authorised, prescribes
 * medication.
 *
 * <p>Extra data: whether this GP is currently allowed to prescribe (a registrar
 * under supervision is not). Extra behaviour: the consultation from
 * {@link #performPrimaryDuty(Patient)} and
 * {@link #prescribeMedication(Patient, String)} from the {@link Prescriber}
 * interface.</p>
 */
public class GeneralPractitioner extends HealthProfessional implements Prescriber {

    /** True when this GP may sign prescriptions without a supervisor. */
    private final boolean canPrescribeMedication;

    /**
     * Creates a GP with an explicit weekly roster.
     *
     * @param id                    numeric identifier, greater than zero
     * @param name                  name of the GP, not blank
     * @param workingDays           days worked every week, not empty
     * @param canPrescribeMedication true when the GP may prescribe medication
     * @throws IllegalArgumentException if the shared arguments are invalid
     */
    public GeneralPractitioner(int id, String name, Set<DayOfWeek> workingDays, boolean canPrescribeMedication) {
        super(id, name, workingDays);
        this.canPrescribeMedication = canPrescribeMedication;
    }

    /**
     * Creates a GP who works the default Monday-to-Friday week.
     *
     * @param id                    numeric identifier, greater than zero
     * @param name                  name of the GP, not blank
     * @param canPrescribeMedication true when the GP may prescribe medication
     * @throws IllegalArgumentException if the shared arguments are invalid
     */
    public GeneralPractitioner(int id, String name, boolean canPrescribeMedication) {
        super(id, name);
        this.canPrescribeMedication = canPrescribeMedication;
    }

    /**
     * Tells whether this GP may prescribe medication.
     *
     * @return true when this GP may prescribe medication
     */
    public boolean canPrescribeMedication() {
        return canPrescribeMedication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRoleDescription() {
        return "General Practitioner (GP)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeAdditionalDetails() {
        return "\n" + detailLine("Prescribing",
                canPrescribeMedication ? "authorised" : "not authorised (supervised registrar)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty(Patient patient) {
        requirePatient(patient);
        return getName() + ", " + getRoleDescription() + ", consulted " + patient.getName()
                + " and recorded a same-day care plan.";
    }

    /**
     * Prescribes medication when this GP is authorised; otherwise the patient is
     * referred to a supervising doctor. Honouring the flag here is what makes
     * the extra field real behaviour instead of decoration.
     *
     * @param patient    patient who receives the medication
     * @param medication name of the medication, not blank
     * @return readable record of the prescription or referral
     * @throws IllegalArgumentException if the patient is null or the medication is blank
     */
    @Override
    public String prescribeMedication(Patient patient, String medication) {
        requirePatient(patient);
        String drug = Prescriber.requireMedication(medication);
        if (!canPrescribeMedication) {
            return getName() + " is not authorised to prescribe; " + patient.getName()
                    + " was referred to a supervising doctor for " + drug + ".";
        }
        return getName() + " prescribed " + drug + " to " + patient.getName() + ".";
    }
}
