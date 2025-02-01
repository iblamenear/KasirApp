import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

public class ProductController {
    public static void loadProducts(DefaultTableModel model) {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
            
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                });
            }
        } catch (SQLException e) {
            UIHelper.showError("Gagal memuat produk!");
        }
    }

    public static void showAddProductDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();

        Object[] fields = {
            "Nama Produk:", nameField,
            "Harga:", priceField,
            "Stok:", stockField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Tambah Produk", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)")) {
                
                stmt.setString(1, nameField.getText());
                stmt.setDouble(2, Double.parseDouble(priceField.getText()));
                stmt.setInt(3, Integer.parseInt(stockField.getText()));
                stmt.executeUpdate();
                
                loadProducts(model);
            } catch (Exception e) {
                UIHelper.showError("Input tidak valid!");
            }
        }
    }

    public static void showRestockDialog(DefaultTableModel model) {
        JTextField idField = new JTextField();
        JTextField qtyField = new JTextField();

        Object[] fields = {
            "ID Produk:", idField,
            "Jumlah Restock:", qtyField
        };

        int option = JOptionPane.showConfirmDialog(
            null, 
            fields, 
            "Restock Manual",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE products SET stock = stock + ? WHERE id = ?")) {
                
                stmt.setInt(1, Integer.parseInt(qtyField.getText()));
                stmt.setInt(2, Integer.parseInt(idField.getText()));
                stmt.executeUpdate();
                
                loadProducts(model);
            } catch (Exception e) {
                UIHelper.showError("Input restock tidak valid!");
            }
        }
    }   

    public static void handleFileRestock(DefaultTableModel model) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                 Connection conn = Database.connect()) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length != 2) continue;
                    
                    try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE products SET stock = stock + ? WHERE id = ?")) {
                        
                        stmt.setInt(1, Integer.parseInt(parts[1].trim()));
                        stmt.setInt(2, Integer.parseInt(parts[0].trim()));
                        stmt.executeUpdate();
                    }
                }
                loadProducts(model);
            } catch (Exception e) {
                UIHelper.showError("File tidak valid!");
            }
        }
    }
}