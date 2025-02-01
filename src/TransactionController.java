import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

public class TransactionController {
    private static double total = 0;
    private static HistoryPanel historyPanel = new HistoryPanel();

    public static void addToCart(String productId, String quantity, JTextArea cartArea) {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT name, price, stock FROM products WHERE id = ?")) {
            
            stmt.setInt(1, Integer.parseInt(productId));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int qty = Integer.parseInt(quantity);
                if (qty <= rs.getInt("stock")) {
                    double price = rs.getDouble("price");
                    total += price * qty;
                    cartArea.append(String.format(
                        "%s - %d x %.2f = %.2f\n",
                        rs.getString("name"),
                        qty,
                        price,
                        price * qty
                    ));
                } else {
                    UIHelper.showError("Stok tidak mencukupi!");
                }
            }
        } catch (Exception e) {
            UIHelper.showError("Input tidak valid!");
        }
    }

    public static void printReceipt(JTextArea cartArea) {
        if (cartArea.getText().isEmpty()) {
            UIHelper.showError("Keranjang kosong!");
            return;
        }
        
        JTextArea receipt = new JTextArea();
        receipt.setText("=== STRUK PEMBELIAN ===\n");
        receipt.append(cartArea.getText());
        receipt.append("\n=======================\n");
        receipt.append("Total: Rp" + total);
        
        JOptionPane.showMessageDialog(
            null, 
            new JScrollPane(receipt), 
            "Struk Pembelian", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void checkout(JTextArea cartArea, 
                               DefaultTableModel productModel, 
                               HistoryPanel historyPanel) {
        if (cartArea.getText().isEmpty()) {
            UIHelper.showError("Keranjang kosong!");
            return;
        }

        try (Connection conn = Database.connect()) {
            String[] items = cartArea.getText().split("\n");
            
            for (String item : items) {
                String[] parts = item.split(" - ");
                if(parts.length < 2) continue;
                
                String productName = parts[0].trim();
                String[] qtyParts = parts[1].split("x");
                int qty = Integer.parseInt(qtyParts[0].trim());
                
                // Update stok
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE name = ?")) {
                    updateStmt.setInt(1, qty);
                    updateStmt.setString(2, productName);
                    updateStmt.executeUpdate();
                }
                
                // Simpan transaksi
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO transactions (product_id, quantity, total) " +
                        "VALUES ((SELECT id FROM products WHERE name = ?), ?, ?)")) {
                    
                    double price = Double.parseDouble(qtyParts[1].split("=")[0].replaceAll("[^\\d.]", ""));
                    insertStmt.setString(1, productName);
                    insertStmt.setInt(2, qty);
                    insertStmt.setDouble(3, price * qty);
                    insertStmt.executeUpdate();
                }
            }
            
            cartArea.setText("");
            JOptionPane.showMessageDialog(null, "Checkout berhasil! Total: Rp" + total);
            total = 0;
            
            // Refresh data
            ProductController.loadProducts(productModel);
            historyPanel.refreshHistory();
            
        } catch (Exception e) {
            UIHelper.showError("Gagal checkout: " + e.getMessage());
        }
    }
}