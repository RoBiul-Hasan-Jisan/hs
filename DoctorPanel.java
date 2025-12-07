import java.sql.*;
import java.util.*;

public class DoctorPanel {
    static Scanner sc = new Scanner(System.in);
    static Stack<Appointment> recentAppointments = new Stack<>();

    public static void showDoctorMenu() {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter your Doctor ID: ");
            int doctorId = sc.nextInt();
            sc.nextLine();

            while (true) {
                System.out.println("\n--- Doctor Panel ---");
                System.out.println("1. View Appointments");
                System.out.println("2. Add Prescription");
                System.out.println("3. View Recently Viewed Appointments");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> viewAppointments(conn, doctorId);
                    case 2 -> addPrescription(conn, doctorId);
                    case 3 -> viewRecentAppointments();
                    case 4 -> {
                        System.out.println("Exiting Doctor Panel...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewAppointments(Connection conn, int doctorId) throws SQLException {
        String sql = """
                    SELECT a.id, p.name AS patient_name, a.appointment_date, a.time, a.reason, a.prescription
                    FROM appointments a
                    JOIN patients p ON a.patient_id = p.id
                    WHERE a.doctor_id = ?
                    """;


        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, doctorId);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n--- Appointments ---");
        while (rs.next()) {
            Appointment appt = new Appointment(
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getString("appointment_date"),
                    rs.getString("time"),
                    rs.getString("reason"),
                    rs.getString("prescription")
            );
            System.out.println(appt);
            recentAppointments.push(appt);
        }
    }

    private static void addPrescription(Connection conn, int doctorId) throws SQLException {
        System.out.print("Enter Appointment ID: ");
        int appointmentId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Prescription: ");
        String prescription = sc.nextLine();

        String sql = "UPDATE appointments SET prescription = ? WHERE id = ? AND doctor_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, prescription);
        ps.setInt(2, appointmentId);
        ps.setInt(3, doctorId);
        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Prescription updated.");
        } else {
            System.out.println("Appointment not found or unauthorized.");
        }
    }

    private static void viewRecentAppointments() {
        if (recentAppointments.isEmpty()) {
            System.out.println("No recent appointments viewed.");
            return;
        }

        System.out.print("How many recent appointments to view? ");
        int count = sc.nextInt();
        sc.nextLine();

        System.out.println("\n--- Recently Viewed Appointments ---");
        int shown = 0;
        ListIterator<Appointment> it = recentAppointments.listIterator(recentAppointments.size());
        while (it.hasPrevious() && shown < count) {
            System.out.println(it.previous());
            shown++;
        }
    }

    static class Appointment {
        int id;
        String patientName, date, time, reason, prescription;

        Appointment(int id, String patientName, String date, String time, String reason, String prescription) {
            this.id = id;
            this.patientName = patientName;
            this.date = date;
            this.time = time;
            this.reason = reason;
            this.prescription = prescription;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Patient: " + patientName +
                    ", Date: " + date + ", Time: " + time +
                    ", Reason: " + reason +
                    ", Prescription: " + (prescription != null ? prescription : "None");
        }
    }
}
