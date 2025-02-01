import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

public class ProductController {
    public static void loadProducts(DefaultTableModel model, String selectedCategory) {
        try (Connection conn = Database.connect()) {
            String query = "SELECT * FROM products";
            if (selectedCategory != null && !selectedCategory.equals("Semua")) {
                query += " WHERE category = ?";
            }
            
            PreparedStatement stmt = conn.prepareStatement(query);
            if (selectedCategory != null && !selectedCategory.equals("Semua")) {
                stmt.setString(1, selectedCategory);
            }
            
            ResultSet rs = stmt.executeQuery();
            model.setRowCount(0); // Reset tabel
            
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                });
            }
        } catch (SQLException e) {
            UIHelper.showError("Gagal memuat produk!");
        }
    }

    public static void showAddProductDialog(DefaultTableModel model, ProductPanel productPanel) {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField stockField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"BahanMentah", "Biji"});
    
        Object[] fields = {
            "Nama Produk:", nameField,
            "Harga:", priceField,
            "Stok:", stockField,
            "Kategori:", categoryCombo
        };
    
        int option = JOptionPane.showConfirmDialog(null, fields, "Tambah Produk", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO products (name, price, stock, category) VALUES (?, ?, ?, ?)")) {
                
                stmt.setString(1, nameField.getText());
                stmt.setDouble(2, Double.parseDouble(priceField.getText()));
                stmt.setInt(3, Integer.parseInt(stockField.getText()));
                stmt.setString(4, (String) categoryCombo.getSelectedItem());
                stmt.executeUpdate();
                
                // Gunakan parameter productPanel
                ProductController.loadProducts(model, productPanel.getSelectedCategory());
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.showError("Input tidak valid! Pastikan format angka benar");
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
                
                loadProducts(model, null);
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
                loadProducts(model, null);
            } catch (Exception e) {
                UIHelper.showError("File tidak valid!");
            }
        }
    }
}