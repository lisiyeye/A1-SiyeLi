/**
 * Patient class stores patient basic information: name and mobile phone number.
 */
public class Patient {
    private final String patientName;
    private final String mobilePhone;

    /**
     * @param patientName patient full name
     * @param mobilePhone patient contact mobile number
     */
    public Patient(String patientName, String mobilePhone) {
        this.patientName = patientName;
        this.mobilePhone = mobilePhone;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    @Override
    public String toString() {
        return "Patient Name: " + patientName + ", Mobile: " + mobilePhone;
    }
}
