import java.time.DayOfWeek;
import java.util.Set;

/**
 * A surgeon: a doctor who operates in a particular surgical specialty and also
 * prescribes post-operative medication.
 *
 * <p>Extra data: the surgical specialty. Extra behaviour: performing the
 * operation and the post-operative prescription.</p>
 */
public class Surgeon extends HealthProfessional implements Prescriber {

    /** Surgical specialty, for example "Cardiothoracic Surgery". */
    private final String surgicalSpecialty;

    /**
     * Creates a surgeon with an explicit weekly roster.
     *
     * @param id              numeric identifier, greater than zero
     * @param name            name of the surgeon, not blank
     * @param workingDays     days worked every week, not empty
     * @param surgicalSpecialty specialty of the surgeon, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(int id, String name, Set<DayOfWeek> workingDays, String surgicalSpecialty) {
        super(id, name, workingDays);
        this.surgicalSpecialty = requireSpecialty(surgicalSpecialty);
    }

    /**
     * Creates a surgeon who works the default Monday-to-Friday week.
     *
     * @param id              numeric identifier, greater than zero
     * @param name            name of the surgeon, not blank
     * @param surgicalSpecialty specialty of the surgeon, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(int id, String name, String surgicalSpecialty) {
        super(id, name);
        this.surgicalSpecialty = requireSpecialty(surgicalSpecialty);
    }

    /**
     * Validates the specialty.
     *
     * @param specialty specialty supplied by the caller
     * @return the trimmed specialty
     * @throws IllegalArgumentException if the specialty is null or blank
     */
    private static String requireSpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new IllegalArgumentException("Surgical specialty must not be blank.");
        }
        return specialty.trim();
    }

    /**
     * Returns the surgical specialty of this surgeon.
     *
     * @return the surgical specialty of this surgeon
     */
    public String getSurgicalSpecialty() {
        return surgicalSpecialty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRoleDescription() {
        return "Surgeon";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeAdditionalDetails() {
        return "\n" + detailLine("Specialty", surgicalSpecialty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty(Patient patient) {
        return performSurgery(patient);
    }

    /**
     * Simulates an operation on the patient.
     *
     * @param patient patient who is operated on
     * @return readable description of the operation
     * @throws IllegalArgumentException if the patient is null
     */
    public String performSurgery(Patient patient) {
        requirePatient(patient);
        return getName() + " performed a " + surgicalSpecialty + " procedure on " + patient.getName() + ".";
    }

    /**
     * Prescribes post-operative medication.
     *
     * @param patient    patient who receives the medication
     * @param medication name of the medication, not blank
     * @return readable record of the prescription
     * @throws IllegalArgumentException if the patient is null or the medication is blank
     */
    @Override
    public String prescribeMedication(Patient patient, String medication) {
        requirePatient(patient);
        String drug = Prescriber.requireMedication(medication);
        return getName() + " prescribed post-operative " + drug + " to " + patient.getName() + ".";
    }
}
