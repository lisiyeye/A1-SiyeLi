/**
 * Appointment stores booking information: patient, health professional, time slot.
 * Implements Comparable to sort appointments by time slot.
 */
public class Appointment implements Comparable<Appointment> {
    private final Patient patient;
    private final HealthProfessional healthProfessional;
    private final String timeSlot;

    /**
     * @param patient patient object for this booking
     * @param healthProfessional assigned health professional
     * @param timeSlot time of appointment e.g "08:00"
     */
    public Appointment(Patient patient, HealthProfessional healthProfessional, String timeSlot) {
        this.patient = patient;
        this.healthProfessional = healthProfessional;
        this.timeSlot = timeSlot;
    }

    public Patient getPatient() {
        return patient;
    }

    public HealthProfessional getHealthProfessional() {
        return healthProfessional;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    @Override
    public int compareTo(Appointment other) {
        return this.timeSlot.compareTo(other.timeSlot);
    }

    @Override
    public String toString() {
        return "\n==== Appointment ====\n" + patient +
                "\nTime Slot: " + timeSlot +
                "\nAssigned Health Professional:\n" + healthProfessional;
    }
}
