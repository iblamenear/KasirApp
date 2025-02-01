import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {
    private static DefaultTableModel tableModel;
    private JTable productTable;
    
    public ProductPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Manajemen Produk"));
        
        initializeTable();
        initializeButtons();
        loadProducts();
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Harga", "Stok"}, 0);
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }

    public static DefaultTableModel getProductModel() {
        return tableModel;
    }

    private void initializeButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        String[] buttons = {"Tambah Produk", "Restock", "Unggah File Restock"};
        for (String text : buttons) {
            JButton btn = new JButton(text);
            UIHelper.styleButton(btn);
            btn.addActionListener(e -> handleProductAction(text));
            buttonPanel.add(btn);
        }
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleProductAction(String action) {
        switch(action) {
            case "Tambah Produk":
                ProductController.showAddProductDialog(tableModel);
                break;
            case "Restock":
                ProductController.showRestockDialog(tableModel);
                break;
            case "Unggah File Restock":
                ProductController.handleFileRestock(tableModel);
                break;
        }
    }

    public void loadProducts() {
        ProductController.loadProducts(tableModel);
    }

    public static DefaultTableModel getModel() {
        return tableModel;
    }
}