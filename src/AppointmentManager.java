import java.util.ArrayList;
import java.util.Collections;

/**
 * AppointmentManager manages collection of appointment objects.
 * Provides add, filter, sort, cancel operations.
 */
public class AppointmentManager {
    private final ArrayList<Appointment> appointmentList;

    public AppointmentManager() {
        appointmentList = new ArrayList<>();
    }

    /**
     * Add new appointment. Reject double‑booking same professional at same time.
     * @param appointment new appointment instance
     */
    public void addAppointment(Appointment appointment) {
        boolean duplicateBooking = false;
        for (Appointment a : appointmentList) {
            if (a.getHealthProfessional().equals(appointment.getHealthProfessional())
                    && a.getTimeSlot().equals(appointment.getTimeSlot())) {
                duplicateBooking = true;
                break;
            }
        }
        if (duplicateBooking) {
            System.out.println("Warning: This time slot is already booked for this health professional.");
        } else {
            appointmentList.add(appointment);
            System.out.println("Appointment added successfully.");
        }
    }

    /**
     * Filter list by health professional id.
     * @param hpId target professional id
     * @return filtered list of appointments
     */
    public ArrayList<Appointment> filterByHealthProfessionalId(int hpId) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : appointmentList) {
            if (a.getHealthProfessional().getId() == hpId) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Filter list by patient mobile phone number.
     * @param phone patient mobile number
     * @return filtered list
     */
    public ArrayList<Appointment> filterByPatientPhone(String phone) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : appointmentList) {
            if (a.getPatient().getMobilePhone().equals(phone)) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Sort given appointment list chronologically by time slot.
     * @param list target list to sort
     */
    public void sortAppointments(ArrayList<Appointment> list) {
        Collections.sort(list);
    }

    /**
     * Cancel appointment by health professional id plus time slot.
     * @param hpId health professional unique id
     * @param timeSlot target booking time
     * @return true if removed successfully
     */
    public boolean cancelByHpIdAndTime(int hpId, String timeSlot) {
        Appointment target = null;
        for (Appointment a : appointmentList) {
            if (a.getHealthProfessional().getId() == hpId && a.getTimeSlot().equals(timeSlot)) {
                target = a;
                break;
            }
        }
        if (target != null) {
            appointmentList.remove(target);
            return true;
        } else {
            System.out.println("Warning: Cannot find this appointment to cancel.");
            return false;
        }
    }

    /**
     * Cancel all appointments belonging to patient with given phone number.
     * @param phone patient mobile number
     * @return true if at least one entry removed
     */
    public boolean cancelByPatientPhone(String phone) {
        boolean removedAny = false;
        ArrayList<Appointment> temp = new ArrayList<>();
        for (Appointment a : appointmentList) {
            if (!a.getPatient().getMobilePhone().equals(phone)) {
                temp.add(a);
            } else {
                removedAny = true;
            }
        }
        appointmentList.clear();
        appointmentList.addAll(temp);
        if (!removedAny) {
            System.out.println("Warning: No appointments found for this phone number.");
        }
        return removedAny;
    }

    /**
     * Get copy of all stored appointments.
     * @return full appointment list
     */
    public ArrayList<Appointment> getAllAppointments() {
        return new ArrayList<>(appointmentList);
    }
}
