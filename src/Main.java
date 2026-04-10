import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("====== Coaching Centre System ======");
        while (true) {
            System.out.println("1. Enrol Student");
            System.out.println("2. Record Fee Payment");
            System.out.println("3. View Batch Students");
            System.out.println("4. Fee Defaulters");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    enrolStudent();
                    break;
                case 2:
                    recordFeePayment();
                    break;
                case 3:
                    viewBatchStudents();
                    break;
                case 4:
                    feeDefaulters();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        }
    }

    private static void enrolStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        
        System.out.println("Available Batches:");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM batches");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getInt("batch_id") + ". " + rs.getString("subject") + " (" + rs.getString("timeslot") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return;
        }

        System.out.print("Enter batch ID to enrol: ");
        int batchId = 0;
        try {
            batchId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid batch ID.");
            return;
        }

        String sql = "INSERT INTO students (name, phone, batch_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setInt(3, batchId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student enrolled successfully.");
            } else {
                System.out.println("Failed to enrol student.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void recordFeePayment() {
        System.out.print("Enter Student ID: ");
        int stuId = 0;
        try {
            stuId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Student ID.");
            return;
        }

        System.out.print("Enter Payment Month (YYYY-MM): ");
        String payMonth = scanner.nextLine();

        String sql = "INSERT INTO fee_payments (stu_id, pay_month) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stuId);
            pstmt.setString(2, payMonth);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment recorded successfully.");
            } else {
                System.out.println("Failed to record payment.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewBatchStudents() {
        System.out.print("Enter Batch ID: ");
        int batchId = 0;
        try {
            batchId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Batch ID.");
            return;
        }

        String sql = "SELECT * FROM students WHERE batch_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                System.out.println("Students in Batch " + batchId + ":");
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: " + rs.getInt("stu_id") + " | Name: " + rs.getString("name") + " | Phone: " + rs.getString("phone"));
                }
                if (!found) {
                    System.out.println("No students found in this batch.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void feeDefaulters() {
        System.out.print("Month: ");
        String currentMonth = scanner.nextLine();

        String sql = "SELECT s.name, b.subject, b.timeslot FROM students s " +
                     "JOIN batches b ON s.batch_id = b.batch_id " +
                     "WHERE s.stu_id NOT IN (SELECT stu_id FROM fee_payments WHERE pay_month = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentMonth);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean found = false;
                System.out.print("Defaulters: ");
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    found = true;
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rs.getString("name"))
                      .append(" (Batch: ")
                      .append(rs.getString("subject"))
                      .append(" ")
                      .append(rs.getString("timeslot"))
                      .append(")");
                }
                if (found) {
                    System.out.println(sb.toString());
                } else {
                    System.out.println("None.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
