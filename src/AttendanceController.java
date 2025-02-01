import javax.swing.*;
import java.sql.*;

public class AttendanceController {
    public static void markAttendance(String employeeCode, JTextArea logArea) {
        try (Connection conn = Database.connect()) {
            // Validasi kode karyawan
            try (PreparedStatement validateStmt = conn.prepareStatement(
                    "SELECT name FROM staff WHERE employee_code = ?")) {
                validateStmt.setString(1, employeeCode);
                ResultSet rs = validateStmt.executeQuery();
                
                if (rs.next()) {
                    // Catat absensi
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO attendance(employee_code) VALUES (?)")) {
                        insertStmt.setString(1, employeeCode);
                        insertStmt.executeUpdate();
                    }
                    
                    // Update log
                    loadAttendance(logArea);
                    JOptionPane.showMessageDialog(null, "Absensi berhasil untuk: " + rs.getString("name"));
                } else {
                    UIHelper.showError("Kode karyawan tidak valid!");
                }
            }
        } catch (SQLException ex) {
            UIHelper.showError("Gagal mencatat absensi!");
        }
    }

    public static void loadAttendance(JTextArea logArea) {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.employee_code, s.name, a.attendance_date " +
                     "FROM attendance a " +
                     "JOIN staff s ON a.employee_code = s.employee_code " +
                     "ORDER BY a.attendance_date DESC")) {
            
            StringBuilder log = new StringBuilder();
            while (rs.next()) {
                log.append(rs.getString("employee_code"))
                   .append("\t")
                   .append(rs.getString("name"))
                   .append("\t")
                   .append(rs.getTimestamp("attendance_date"))
                   .append("\n");
            }
            logArea.setText(log.toString());
        } catch (SQLException ex) {
            UIHelper.showError("Gagal memuat data absensi!");
        }
    }
}