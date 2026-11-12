import java.util.ArrayList;

public class AssignmentOne {
    public static void main(String[] args) {
        ArrayList<String> monWed = new ArrayList<>();
        monWed.add("Monday");
        monWed.add("Wednesday");

        ArrayList<String> tueThu = new ArrayList<>();
        tueThu.add("Tuesday");
        tueThu.add("Thursday");

        ArrayList<HealthProfessional> allProfessionals = new ArrayList<>();
        allProfessionals.add(new GeneralPractitioner(101, "Dr Amy", monWed, true));
        allProfessionals.add(new GeneralPractitioner(102, "Dr Bob", tueThu, true));
        allProfessionals.add(new GeneralPractitioner(103, "Dr Chris", monWed, true));
        allProfessionals.add(new Nurse(201, "Nurse Lisa", tueThu, "Level3"));
        allProfessionals.add(new Surgeon(301, "Dr Sam", monWed, "Heart Surgery"));
        allProfessionals.add(new Dietitian(401, "Ms Zoe", tueThu, "Child Nutrition"));

        System.out.println("==== All Health Professionals ====");
        for (HealthProfessional hp : allProfessionals) {
            System.out.println(hp);
            System.out.println("--------------------------------");
        }

        System.out.println("\n\n==== START APPOINTMENT SYSTEM DEMO ====");
        AppointmentManager manager = new AppointmentManager();

        Patient pat1 = new Patient("Tom", "0400111111");
        Patient pat2 = new Patient("Lily", "0400222222");
        Patient pat3 = new Patient("Jack", "0400333333");

        System.out.println("\n--- Adding valid appointments ---");
        manager.addAppointment(new Appointment(pat1, allProfessionals.get(0), "08:00"));
        manager.addAppointment(new Appointment(pat2, allProfessionals.get(1), "09:30"));
        manager.addAppointment(new Appointment(pat3, allProfessionals.get(3), "10:00"));

        System.out.println("\n--- Test duplicate booking (should show warning) ---");
        manager.addAppointment(new Appointment(pat2, allProfessionals.get(0), "08:00"));

        ArrayList<Appointment> allApps = manager.getAllAppointments();
        manager.sortAppointments(allApps);
        System.out.println("\n--- All appointments sorted by time ---");
        for (Appointment app : allApps) {
            System.out.println(app);
        }

        System.out.println("\n--- Filter appointments by HP id 101 ---");
        ArrayList<Appointment> filteredHp = manager.filterByHealthProfessionalId(101);
        for (Appointment app : filteredHp) {
            System.out.println(app);
        }

        System.out.println("\n--- Filter appointments by patient phone 0400222222 ---");
        ArrayList<Appointment> filteredPhone = manager.filterByPatientPhone("0400222222");
        for (Appointment app : filteredPhone) {
            System.out.println(app);
        }

        System.out.println("\n--- Cancel appointment hpId 101, time 08:00 ---");
        manager.cancelByHpIdAndTime(101, "08:00");

        System.out.println("\n--- Try cancel non-existent appointment ---");
        manager.cancelByHpIdAndTime(999, "14:00");

        System.out.println("\n--- Appointment list after cancellation ---");
        ArrayList<Appointment> afterCancel = manager.getAllAppointments();
        manager.sortAppointments(afterCancel);
        for (Appointment app : afterCancel) {
            System.out.println(app);
        }

        System.out.println("\n--- Cancel all appointments for phone 0400333333 ---");
        manager.cancelByPatientPhone("0400333333");

        System.out.println("\n--- Final appointment list ---");
        ArrayList<Appointment> finalList = manager.getAllAppointments();
        manager.sortAppointments(finalList);
        for (Appointment app : finalList) {
            System.out.println(app);
        }
    }
}
