import java.time.DayOfWeek;
import java.util.Set;

/**
 * A registered nurse. Nurses look after patients, take blood samples and other
 * observations; they do not prescribe medication, which is why this class does
 * not implement {@link Prescriber}.
 *
 * <p>Extra data: the certification level of the nurse. Extra behaviour: taking
 * a blood sample.</p>
 */
public class Nurse extends HealthProfessional {

    /** Certification level, for example "Level 3". */
    private final String certificationLevel;

    /**
     * Creates a nurse with an explicit weekly roster.
     *
     * @param id                 numeric identifier, greater than zero
     * @param name               name of the nurse, not blank
     * @param workingDays        days worked every week, not empty
     * @param certificationLevel certification level, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(int id, String name, Set<DayOfWeek> workingDays, String certificationLevel) {
        super(id, name, workingDays);
        this.certificationLevel = requireCertificationLevel(certificationLevel);
    }

    /**
     * Creates a nurse who works the default Monday-to-Friday week.
     *
     * @param id                 numeric identifier, greater than zero
     * @param name               name of the nurse, not blank
     * @param certificationLevel certification level, not blank
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(int id, String name, String certificationLevel) {
        super(id, name);
        this.certificationLevel = requireCertificationLevel(certificationLevel);
    }

    /**
     * Validates the certification level.
     *
     * @param level level supplied by the caller
     * @return the trimmed level
     * @throws IllegalArgumentException if the level is null or blank
     */
    private static String requireCertificationLevel(String level) {
        if (level == null || level.trim().isEmpty()) {
            throw new IllegalArgumentException("Nurse certification level must not be blank.");
        }
        return level.trim();
    }

    /**
     * Returns the certification level of this nurse.
     *
     * @return the certification level of this nurse
     */
    public String getCertificationLevel() {
        return certificationLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRoleDescription() {
        return "Registered Nurse";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeAdditionalDetails() {
        return "\n" + detailLine("Certification", certificationLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty(Patient patient) {
        return collectBloodSample(patient);
    }

    /**
     * Simulates taking a blood sample from the patient.
     *
     * @param patient patient who gives the sample
     * @return readable description of the procedure
     * @throws IllegalArgumentException if the patient is null
     */
    public String collectBloodSample(Patient patient) {
        requirePatient(patient);
        return getName() + " (" + certificationLevel + ") collected a blood sample from "
                + patient.getName() + " for pathology.";
    }
}
