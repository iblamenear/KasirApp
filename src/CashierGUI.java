import javax.swing.*;
import java.awt.*;

public class CashierGUI extends JFrame {
    private ProductPanel productPanel;
    private TransactionPanel transactionPanel;
    private AttendancePanel attendancePanel;
    private HistoryPanel historyPanel;
    

    public CashierGUI() {
        setTitle("Kasir - Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Inisialisasi komponen SEBELUM digunakan
        historyPanel = new HistoryPanel(); // Inisialisasi pertama
        productPanel = new ProductPanel();
        transactionPanel = new TransactionPanel(historyPanel, productPanel);
        attendancePanel = new AttendancePanel();

        // Header
        add(new HeaderPanel(), BorderLayout.NORTH);
        
        // Main Content
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Transaksi
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(productPanel);
        mainSplitPane.setRightComponent(transactionPanel);
        tabbedPane.addTab("Transaksi", mainSplitPane);
        
        // Tab Riwayat
        tabbedPane.addTab("Riwayat", historyPanel);
        
        // Tab Absensi
        tabbedPane.addTab("Absensi", attendancePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
}