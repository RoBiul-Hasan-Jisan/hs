import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Hospital Management System =====");
            System.out.println("1. Admin Panel");
            System.out.println("2. Doctor Panel");
            System.out.println("3. Patient Panel");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> AdminPanel.showAdminPanel();
                case 2 -> DoctorPanel.showDoctorMenu();
                case 3 -> PatientPanel.showPatientMenu();
                case 4 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}