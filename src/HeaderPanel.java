import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HeaderPanel extends JPanel {
    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(70, 130, 180));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel title = new JLabel("Sistem Kasir Perkebunan", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton logoutBtn = new JButton("Logout");
        UIHelper.styleButton(logoutBtn);
        logoutBtn.addActionListener(this::handleLogout);
        
        add(title, BorderLayout.CENTER);
        add(logoutBtn, BorderLayout.EAST);
    }

    private void handleLogout(ActionEvent e) {
        System.exit(0);
    }
}