/**
 * Capability interface for health professionals who are allowed to prescribe
 * medication.
 *
 * <p>Why an interface instead of one more method on
 * {@link HealthProfessional}? Because the ability does not belong to the whole
 * hierarchy: in this clinic nurses and dietitians cannot prescribe, and the
 * ability cuts across branches of the inheritance tree (a general practitioner
 * and a surgeon are different types but both prescribe). An interface models
 * exactly that: "any object that can do this", independent of its position in
 * the class hierarchy.</p>
 *
 * <p>Client code can therefore ask {@code hp instanceof Prescriber} and use the
 * capability without knowing the concrete class.</p>
 */
public interface Prescriber {

    /**
     * Prescribes a medication to a patient.
     *
     * @param patient    patient who receives the medication
     * @param medication name of the medication, must not be blank
     * @return readable record of the prescription
     * @throws IllegalArgumentException if the patient is null or the medication is blank
     */
    String prescribeMedication(Patient patient, String medication);

    /**
     * Validates and trims a medication name. It is a static helper on the
     * interface so both implementations share exactly one validation rule.
     *
     * @param medication medication name supplied by the caller
     * @return the trimmed medication name
     * @throws IllegalArgumentException if the medication is null or blank
     */
    static String requireMedication(String medication) {
        if (medication == null || medication.trim().isEmpty()) {
            throw new IllegalArgumentException("Medication name must not be blank.");
        }
        return medication.trim();
    }
}
