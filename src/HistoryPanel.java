import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HistoryPanel extends JPanel {
    private DefaultTableModel model;
    private JTable historyTable;

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi"));
        
        model = new DefaultTableModel(
            new String[]{"ID", "Produk", "Jumlah", "Total", "Tanggal"}, 0
        );
        historyTable = new JTable(model);
        
        add(new JScrollPane(historyTable), BorderLayout.CENTER);
        loadHistory();
    }

    private void loadHistory() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT t.id, p.name, t.quantity, t.total, t.transaction_date " +
                 "FROM transactions t " +
                 "JOIN products p ON t.product_id = p.id " +
                 "ORDER BY t.transaction_date DESC")) {
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("total"),
                    rs.getTimestamp("transaction_date")
                });
            }
        } catch (SQLException e) {
            UIHelper.showError("Gagal memuat riwayat transaksi!");
        }
    }

    public void refreshHistory() {
        model.setRowCount(0);
        loadHistory();
    }
}