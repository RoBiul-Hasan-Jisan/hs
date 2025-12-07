import java.sql.*;
import java.util.*;
import java.sql.Date;


public class PatientPanel {
    static Scanner sc = new Scanner(System.in);

    static Queue<Appointment> appointmentQueue = new LinkedList<>();
    static List<Doctor> doctorList = new ArrayList<>();

    public static void showPatientMenu() {
        try (Connection conn = DBConnection.getConnection()) {
            while (true) {
                System.out.println("\n--- Patient Menu ---");
                System.out.println("1. Register");
                System.out.println("2. Book Appointment");
                System.out.println("3. View My Appointments");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> registerPatient();
                    case 2 -> bookAppointment(conn);
                    case 3 -> viewMyAppointments();
                    case 4 -> {
                        System.out.println("Exiting Patient Panel...");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerPatient() {
    try (Connection conn = DBConnection.getConnection()) {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine(); 

        System.out.print("Enter Gender (Male/Female/Other): ");
        String gender = sc.nextLine();

        System.out.print("Enter Address: ");
        String address = sc.nextLine();

        System.out.print("Enter Contact: ");
        String contact = sc.nextLine();

        System.out.print("Set a username: ");
        String username = sc.nextLine();

        System.out.print("Set a password: ");
        String password = sc.nextLine();

        
        String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'patient')";
        PreparedStatement psUser = conn.prepareStatement(userSql);
        psUser.setString(1, username);
        psUser.setString(2, password);
        psUser.executeUpdate();

        
        String patientSql = "INSERT INTO patients (name, age, gender, address, contact, password) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement psPatient = conn.prepareStatement(patientSql, Statement.RETURN_GENERATED_KEYS);
        psPatient.setString(1, name);
        psPatient.setInt(2, age);
        psPatient.setString(3, gender);
        psPatient.setString(4, address);
        psPatient.setString(5, contact);
        psPatient.setString(6, password);
        psPatient.executeUpdate();

        ResultSet rs = psPatient.getGeneratedKeys();
        if (rs.next()) {
            int patientId = rs.getInt(1);  
            System.out.println("Patient registered successfully!");
            System.out.println("Your Patient ID is: " + patientId);
        } else {
            System.out.println("Patient registered, but failed to retrieve ID.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private static void bookAppointment(Connection conn) throws SQLException {
        System.out.print("Enter your Patient ID: ");
        int patientId = sc.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter Time (HH:MM:SS): ");
        String time = sc.nextLine();
        System.out.print("Enter Reason: ");
        String reason = sc.nextLine();

        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, time, reason) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, patientId);
        ps.setInt(2, doctorId);
        ps.setString(3, date);
        ps.setString(4, time);
        ps.setString(5, reason);
        ps.executeUpdate();

        appointmentQueue.offer(new Appointment(patientId, doctorId, date, time, reason));
        System.out.println("Appointment booked");
    }

   private static void viewMyAppointments() {
    System.out.print("Enter your Patient ID: ");
    int patientId = sc.nextInt();
    sc.nextLine(); 

    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT a.id, d.name AS doctor_name, a.appointment_date, a.time, a.reason, a.prescription " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.patient_id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, patientId);
        ResultSet rs = ps.executeQuery();

        System.out.println("--- Your Appointments ---");
        while (rs.next()) {
            int apptId = rs.getInt("id");
            String docName = rs.getString("doctor_name");
            Date date = rs.getDate("appointment_date");
            Time time = rs.getTime("time");
            String reason = rs.getString("reason");
            String prescription = rs.getString("prescription");

            System.out.println("Appointment ID: " + apptId);
            System.out.println("Doctor: " + docName);
            System.out.println("Date: " + date + " | Time: " + time);
            System.out.println("Reason: " + reason);
            System.out.println("Prescription: " + (prescription == null ? "N/A" : prescription));
            System.out.println("-----------------------------");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    




    static class Appointment {
        int patientId, doctorId;
        String date, time, reason;

        Appointment(int pid, int did, String d, String t, String r) {
            patientId = pid;
            doctorId = did;
            date = d;
            time = t;
            reason = r;
        }
    }

    static class Doctor {
        int id;
        String name, specialization, availability;

        Doctor(int id, String name, String spec, String avail) {
            this.id = id;
            this.name = name;
            this.specialization = spec;
            this.availability = avail;
        }

        @Override
        public String toString() {
            return "ID: " + id + ", Name: " + name +
                    ", Specialization: " + specialization +
                    ", Available: " + availability;
        }
    }
}
