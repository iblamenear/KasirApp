import javax.swing.*;
import java.awt.*;

public class AttendancePanel extends JPanel {
    private JTextArea logArea;
    private JTextField employeeCodeField;
    
    public AttendancePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Absensi Karyawan"));
        
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        employeeCodeField = new JTextField();
        JButton absenBtn = new JButton("Absen Sekarang");
        UIHelper.styleButton(absenBtn);
        
        inputPanel.add(employeeCodeField, BorderLayout.CENTER);
        inputPanel.add(absenBtn, BorderLayout.EAST);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        
        absenBtn.addActionListener(e -> {
            AttendanceController.markAttendance(
                employeeCodeField.getText(),
                logArea
            );
            employeeCodeField.setText("");
        });
        
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }
}