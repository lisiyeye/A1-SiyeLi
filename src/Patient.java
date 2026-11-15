import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A patient of the clinic. The brief only requires a name and a mobile number,
 * so the patient is a small immutable value class rather than part of the
 * professional hierarchy.
 *
 * <p>Design notes:</p>
 * <ul>
 *   <li>Both fields are private and final, and both are validated in the
 *       constructor, so an incomplete patient can never exist.</li>
 *   <li>The mobile number is the natural identity of a patient inside the
 *       clinic, so {@link #equals(Object)} and {@link #hashCode()} use it. That
 *       lets the appointment manager filter and cancel bookings by patient
 *       object as well as by phone number.</li>
 * </ul>
 */
public final class Patient {

    /** A mobile number accepted by the clinic: ten digits starting with zero. */
    private static final Pattern MOBILE_NUMBER = Pattern.compile("0\\d{9}");

    /** Full name of the patient. */
    private final String name;

    /** Mobile phone number, stored as ten digits without spaces. */
    private final String mobilePhone;

    /**
     * Creates a patient.
     *
     * @param name        full name, must not be blank
     * @param mobilePhone mobile number with ten digits, for example "0400111222"
     * @throws IllegalArgumentException if the name is blank or the number is malformed
     */
    public Patient(String name, String mobilePhone) {
        this.name = requireName(name);
        this.mobilePhone = requireMobilePhone(mobilePhone);
    }

    /**
     * Validates and trims the patient name.
     *
     * @param name candidate name
     * @return the trimmed name
     * @throws IllegalArgumentException if the name is null or blank
     */
    private static String requireName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name must not be blank.");
        }
        return name.trim();
    }

    /**
     * Validates the mobile number.
     *
     * @param mobilePhone candidate number
     * @return the number without spaces
     * @throws IllegalArgumentException if the number is null, contains anything
     *                                  but digits or does not have ten digits
     */
    private static String requireMobilePhone(String mobilePhone) {
        if (mobilePhone == null) {
            throw new IllegalArgumentException("Patient mobile phone must not be null.");
        }
        String cleaned = mobilePhone.replace(" ", "");
        if (!MOBILE_NUMBER.matcher(cleaned).matches()) {
            throw new IllegalArgumentException(
                    "Patient mobile phone must be ten digits starting with 0 but was \"" + mobilePhone + "\".");
        }
        return cleaned;
    }

    /**
     * Returns the full name of the patient.
     *
     * @return the full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mobile number as stored.
     *
     * @return the mobile number as stored, ten digits without spaces
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * Returns the mobile number grouped for reading.
     *
     * @return the mobile number grouped for reading, for example "0400 111 111"
     */
    public String getFormattedMobilePhone() {
        return mobilePhone.substring(0, 4) + " " + mobilePhone.substring(4, 7) + " " + mobilePhone.substring(7);
    }

    /**
     * Two patients are the same patient when their mobile numbers match: the
     * number is what the clinic uses to find a booking.
     *
     * @param other object to compare with
     * @return true when the other object is a patient with the same number
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Patient)) {
            return false;
        }
        return mobilePhone.equals(((Patient) other).mobilePhone);
    }

    /**
     * @return hash code of the mobile number, consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(mobilePhone);
    }

    /**
     * @return readable one-line description of the patient
     */
    @Override
    public String toString() {
        return name + " (" + getFormattedMobilePhone() + ")";
    }
}
