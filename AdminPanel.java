import java.sql.*;
import java.util.Scanner;

public class AdminPanel {
    static Scanner sc = new Scanner(System.in);
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin@123";

    public static void showAdminPanel() {
        if (!login()) {
            System.out.println("Access Denied. Exiting...");
            return;
        }

        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Remove Doctor");
            System.out.println("4. Update Doctor");
            System.out.println("5. View All Patients");
            System.out.println("6. Remove Patient");
            System.out.println("7. Update Patient");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addDoctor();
                case 2 -> viewDoctors();
                case 3 -> removeDoctor();
                case 4 -> updateDoctor();
                case 5 -> viewPatients();
                case 6 -> removePatient();
                case 7 -> updatePatient();
                case 8 -> {
                    System.out.println("Exiting Admin Panel...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static boolean login() {
        System.out.print("Enter username: ");
        String user = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();
        return USERNAME.equals(user) && PASSWORD.equals(pass);
    }



    public static void addDoctor() {
        try {
            System.out.print("Enter Doctor Username: ");
            String username = sc.nextLine();
            System.out.print("Set a password: ");
            String password = sc.nextLine();
            System.out.print("Enter Doctor Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Specialization: ");
            String specialization = sc.nextLine();
            System.out.print("Enter Contact: ");
            String contact = sc.nextLine();
            System.out.print("Enter Availability: ");
            String availability = sc.nextLine();

            Connection conn = DBConnection.getConnection();

            String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'doctor')";
            PreparedStatement ps1 = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, username);
            ps1.setString(2, password);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            String doctorQuery = "INSERT INTO doctors (id, name, specialization, contact, availability, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(doctorQuery);
            ps2.setInt(1, userId);
            ps2.setString(2, name);
            ps2.setString(3, specialization);
            ps2.setString(4, contact);
            ps2.setString(5, availability);
            ps2.setString(6, password);
            ps2.executeUpdate();

            System.out.println("Doctor added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    public static void viewDoctors() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM doctors";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n\nDoctor List:");
            while (rs.next()) {
                System.out.println("-------------------------");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Specialization: " + rs.getString("specialization"));
                System.out.println("Contact: " + rs.getString("contact"));
                System.out.println("Availability: " + rs.getString("availability"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removeDoctor() {
    try (Connection conn = DBConnection.getConnection()) {
        System.out.print("Enter Doctor ID to remove: ");
        int id = sc.nextInt();

        String sql1 = "DELETE FROM doctors WHERE id = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setInt(1, id);
        int r1 = ps1.executeUpdate();

        String sql2 = "DELETE FROM users WHERE role = 'doctor' AND id = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.setInt(1, id);
        int r2 = ps2.executeUpdate();

        System.out.println((r1 > 0 && r2 > 0) ? "Doctor removed." : "Doctor not found.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private static void updateDoctor() {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Doctor ID to update: ");
            int id = sc.nextInt(); sc.nextLine();

            System.out.print("Enter new Name: ");
            String name = sc.nextLine();
            System.out.print("Enter new Specialization: ");
            String specialization = sc.nextLine();

            String updateDoctor = "UPDATE doctors SET name = ?, specialization = ? WHERE id = ?";
            PreparedStatement doctorPs = conn.prepareStatement(updateDoctor);
            doctorPs.setString(1, name);
            doctorPs.setString(2, specialization);
            doctorPs.setInt(3, id);
            int rows = doctorPs.executeUpdate();

            System.out.println(rows > 0 ? "Doctor updated." : "Doctor not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewPatients() {
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM patients";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);


            System.out.println("\n\nPatient List:");
            while (rs.next()) {
                System.out.println("-------------------------");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Address: " + rs.getString("address"));
                System.out.println("Contact: " + rs.getString("contact"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removePatient() {
    try (Connection conn = DBConnection.getConnection()) {
        System.out.print("Enter Patient ID to remove: ");
        int id = sc.nextInt();

        String sql1 = "DELETE FROM patients WHERE id = ?";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setInt(1, id);
        int r1 = ps1.executeUpdate();
        String sql2 = "DELETE FROM users WHERE role = 'patient' AND id = ?";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ps2.setInt(1, id);
        int r2 = ps2.executeUpdate();

        System.out.println((r1 > 0 && r2 > 0) ? "Patient removed." : "Patient not found.");
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private static void updatePatient() {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Enter Patient ID to update: ");
            int id = sc.nextInt(); sc.nextLine();

            System.out.print("Enter new Name: ");
            String name = sc.nextLine();
            System.out.print("Enter new Age: ");
            int age = sc.nextInt(); sc.nextLine();
            System.out.print("Enter new Gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter new Contact: ");
            String contact = sc.nextLine();
            System.out.print("Enter new Address: ");
            String address = sc.nextLine();

            String updatePatient = "UPDATE patients SET name = ?, age = ?, gender = ?, contact = ?, address = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(updatePatient);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, contact);
            ps.setString(5, address);
            ps.setInt(6, id);
            int rows = ps.executeUpdate();

            System.out.println(rows > 0 ? "Patient updated." : "Patient not found.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}