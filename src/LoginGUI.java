import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    public LoginGUI() {
        setTitle("Login Sistem Kasir");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        // Form Login
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);
        panel.add(exitButton);

        add(panel);

        // Event Listener Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                // Validasi sederhana (bisa diubah sesuai kebutuhan)
                if (username.equals("admin") && password.equals("admin123")) {
                    JOptionPane.showMessageDialog(null, "Login Berhasil!");
                    dispose(); // Tutup jendela login
                    new CashierGUI(); // Buka dashboard kasir
                } else {
                    JOptionPane.showMessageDialog(null, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Event Listener Exit Button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }
}
