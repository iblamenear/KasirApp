import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JComboBox<String> categoryFilter;
    
    public ProductPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Manajemen Produk"));
        
        initializeTable();
        initializeButtons();
        initializeCategoryFilter();
        loadProducts();
    }

    private void initializeCategoryFilter() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryFilter = new JComboBox<>(new String[]{"Semua", "BahanMentah", "Biji"});
        categoryFilter.addActionListener(e -> loadProducts());
        filterPanel.add(new JLabel("Filter Kategori:"));
        filterPanel.add(categoryFilter);
        add(filterPanel, BorderLayout.NORTH);
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama", "Harga", "Stok", "Kategori"}, 0
        );
        productTable = new JTable(tableModel);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }

    public DefaultTableModel getProductModel() {
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
                ProductController.showAddProductDialog(tableModel, this);
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
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        ProductController.loadProducts(tableModel, getSelectedCategory());
    }

    public DefaultTableModel getModel() {
        return tableModel;
    }

    public String getSelectedCategory() {
        return (String) categoryFilter.getSelectedItem();
    }
}