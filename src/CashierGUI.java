import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CashierGUI extends JFrame { // Class Cashier GUI
    private JTable productTable; // Atribut untuk Table
    private DefaultTableModel productTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextArea cartTextArea;
    private double totalHarga = 0;

    public CashierGUI() {
        setTitle("Kasir - Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadProductData();

        setVisible(true);
    }

    private void initUI() {
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
    
        JSplitPane splitPane = createSplitPanel();
        add(splitPane, BorderLayout.CENTER);
    
        // Atur warna dasar untuk seluruh frame
        getContentPane().setBackground(new Color(173, 216, 230)); // Warna biru muda
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setBackground(new Color(70, 130, 180)); // Warna biru gelap
        JLabel welcomeLabel = new JLabel("Selamat Datang di Sistem Kasir");
        welcomeLabel.setForeground(Color.WHITE); // Teks putih
        headerPanel.add(welcomeLabel);
    
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton); // Terapkan gaya tombol
        headerPanel.add(logoutButton);
    
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Logout Berhasil!");
            System.exit(0);
        });
    
        return headerPanel;
    }

    private JSplitPane createSplitPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        JPanel productPanel = createProductPanel();
        splitPane.setLeftComponent(productPanel);

        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        return splitPane;
    }

    private JPanel createProductPanel() {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createTitledBorder("Daftar Produk"));
        productPanel.setBackground(new Color(224, 255, 255)); // Warna biru muda
    
        productTableModel = new DefaultTableModel(new String[]{"ID", "Nama Produk", "Harga", "Stok"}, 0);
        productTable = new JTable(productTableModel);
        productTable.setBackground(new Color(240, 248, 255)); // Warna tabel
        productTable.setForeground(Color.BLACK);
        productTable.setSelectionBackground(new Color(70, 130, 180));
        productTable.setSelectionForeground(Color.WHITE);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productPanel.add(productScrollPane, BorderLayout.CENTER);
    
        // Tambahkan tombol Tambah Produk dan Restock Produk
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230)); // Warna biru muda
    
        JButton addProductButton = new JButton("Tambah Produk");
        styleButton(addProductButton);
        buttonPanel.add(addProductButton);
    
        JButton restockButton = new JButton("Restock Produk");
        styleButton(restockButton);
        buttonPanel.add(restockButton);
    
        productPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        // Event Listener untuk tombol Tambah Produk
        addProductButton.addActionListener(e -> handleAddNewProduct());
    
        // Event Listener untuk tombol Restock Produk
        restockButton.addActionListener(e -> handleRestockProduct());
    
        return productPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel transactionPanel = createTransactionPanel();
        rightPanel.add(transactionPanel, BorderLayout.NORTH);

        JPanel historyPanel = createHistoryPanel();
        rightPanel.add(historyPanel, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createTransactionPanel() {
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Keranjang"));
        transactionPanel.setBackground(new Color(224, 255, 255)); // Warna biru muda
    
        cartTextArea = new JTextArea(10, 30);
        cartTextArea.setEditable(false);
        cartTextArea.setBackground(new Color(240, 248, 255));
        cartTextArea.setForeground(Color.BLACK);
    
        JScrollPane cartScrollPane = new JScrollPane(cartTextArea);
        transactionPanel.add(cartScrollPane, BorderLayout.CENTER);
    
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(173, 216, 230)); // Warna biru muda
        inputPanel.add(new JLabel("ID Produk: "));
        JTextField productIdField = new JTextField(5);
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Jumlah: "));
        JTextField quantityField = new JTextField(5);
        inputPanel.add(quantityField);
        JButton addButton = new JButton("Tambah");
        styleButton(addButton); // Terapkan gaya tombol
        inputPanel.add(addButton);
        JButton checkoutButton = new JButton("Checkout");
        styleButton(checkoutButton); // Terapkan gaya tombol
        inputPanel.add(checkoutButton);
    
        JButton printReceiptButton = new JButton("Cetak Struk");
        styleButton(printReceiptButton); // Terapkan gaya tombol
        inputPanel.add(printReceiptButton);
    
        transactionPanel.add(inputPanel, BorderLayout.SOUTH);
    
        addButton.addActionListener(e -> handleAddToCart(productIdField, quantityField));
        checkoutButton.addActionListener(e -> handleCheckout());
        printReceiptButton.addActionListener(e -> printReceipt());
    
        return transactionPanel;
    }

    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Transaksi"));
        historyPanel.setBackground(new Color(224, 255, 255)); // Warna biru muda
    
        JButton viewHistoryButton = new JButton("Lihat Riwayat Transaksi");
        styleButton(viewHistoryButton); // Terapkan gaya tombol
        historyPanel.add(viewHistoryButton, BorderLayout.NORTH);
    
        historyTableModel = new DefaultTableModel(new String[]{"ID", "Nama Produk", "Jumlah", "Total", "Tanggal"}, 0);
        historyTable = new JTable(historyTableModel);
        historyTable.setBackground(new Color(240, 248, 255));
        historyTable.setForeground(Color.BLACK);
        historyTable.setSelectionBackground(new Color(70, 130, 180));
        historyTable.setSelectionForeground(Color.WHITE);
    
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
    
        viewHistoryButton.addActionListener(e -> loadTransactionHistory());
    
        return historyPanel;
    }

    private void handleAddToCart(JTextField productIdField, JTextField quantityField) {
        try {
            int productId = Integer.parseInt(productIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                throw new NumberFormatException("Jumlah harus lebih besar dari nol.");
            }

            addToCart(productId, quantity);
            productIdField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Masukkan angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCheckout() {
        if (totalHarga > 0) {
            checkout();
            productTableModel.setRowCount(0);
            loadProductData();
            cartTextArea.setText("");
            totalHarga = 0;
        } else {
            JOptionPane.showMessageDialog(null, "Keranjang kosong!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductData() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");
                productTableModel.addRow(new Object[]{id, name, price, stock});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat data produk!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToCart(int productId, int quantity) {
        try (Connection conn = Database.connect();
             PreparedStatement productStmt = conn.prepareStatement("SELECT * FROM products WHERE id = ?")) {

            productStmt.setInt(1, productId);
            try (ResultSet rs = productStmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");

                    if (quantity <= stock) {
                        totalHarga += price * quantity;
                        cartTextArea.append(name + " - " + quantity + " x " + price + " = " + (price * quantity) + "\n");
                    } else {
                        JOptionPane.showMessageDialog(null, "Stok tidak mencukupi!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Produk tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan ke keranjang!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkout() {
        try (Connection conn = Database.connect()) {
            String[] cartItems = cartTextArea.getText().split("\n");
    
            for (String item : cartItems) {
                // Parsing format item seperti "Nama Produk - Jumlah x Harga = Total"
                String[] details = item.split(" - ");
                if (details.length < 2) continue; // Abaikan jika format tidak sesuai
                
                String name = details[0];
                String[] qtyAndTotal = details[1].split(" x ");
                if (qtyAndTotal.length < 2) continue; // Abaikan jika format tidak sesuai
                
                int quantity = Integer.parseInt(qtyAndTotal[0].trim());
                String[] priceAndTotal = qtyAndTotal[1].split(" = ");
                if (priceAndTotal.length < 2) continue; // Abaikan jika format tidak sesuai
                
                double price = Double.parseDouble(priceAndTotal[0].trim());
    
                // Update stok di database
                String updateStockQuery = "UPDATE products SET stock = stock - ? WHERE name = ?";
                PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery);
                updateStockStmt.setInt(1, quantity);
                updateStockStmt.setString(2, name);
                updateStockStmt.executeUpdate();
    
                // Simpan transaksi ke database
                String transactionQuery = "INSERT INTO transactions (product_id, quantity, total) VALUES ((SELECT id FROM products WHERE name = ?), ?, ?)";
                PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setString(1, name);
                transactionStmt.setInt(2, quantity);
                transactionStmt.setDouble(3, price * quantity);
                transactionStmt.executeUpdate();
            }
    
            JOptionPane.showMessageDialog(null, "Checkout berhasil! Total: Rp " + totalHarga);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void handleAddNewProduct() {
        JTextField productNameField = new JTextField();
        JTextField productPriceField = new JTextField();
        JTextField productStockField = new JTextField();

        Object[] inputFields = {
            "Nama Produk:", productNameField,
            "Harga Produk:", productPriceField,
            "Stok Produk:", productStockField
        };

        int option = JOptionPane.showConfirmDialog(null, inputFields, "Tambah Produk Baru", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = productNameField.getText();
            double price;
            int stock;

            try {
                price = Double.parseDouble(productPriceField.getText());
                stock = Integer.parseInt(productStockField.getText());

                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, price, stock) VALUES (?, ?, ?)");) {
                    stmt.setString(1, name);
                    stmt.setDouble(2, price);
                    stmt.setInt(3, stock);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Produk baru berhasil ditambahkan!");
                    productTableModel.setRowCount(0);
                    loadProductData();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Harga dan stok harus berupa angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal menambahkan produk baru!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRestockProduct() {
        JTextField productIdField = new JTextField();
        JTextField restockAmountField = new JTextField();

        Object[] inputFields = {
            "ID Produk:", productIdField,
            "Jumlah Restock:", restockAmountField
        };

        int option = JOptionPane.showConfirmDialog(null, inputFields, "Restock Produk", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int productId;
            int restockAmount;

            try {
                productId = Integer.parseInt(productIdField.getText());
                restockAmount = Integer.parseInt(restockAmountField.getText());

                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement("UPDATE products SET stock = stock + ? WHERE id = ?");) {
                    stmt.setInt(1, restockAmount);
                    stmt.setInt(2, productId);
                    int rowsUpdated = stmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(null, "Restock berhasil!");
                        productTableModel.setRowCount(0);
                        loadProductData();
                    } else {
                        JOptionPane.showMessageDialog(null, "ID Produk tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID dan jumlah restock harus berupa angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Gagal melakukan restock produk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTransactionHistory() {
        historyTableModel.setRowCount(0);
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT t.id, p.name, t.quantity, t.total, t.transaction_date FROM transactions t JOIN products p ON t.product_id = p.id")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double total = rs.getDouble("total");
                Timestamp date = rs.getTimestamp("transaction_date");
                historyTableModel.addRow(new Object[]{id, name, quantity, total, date});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat riwayat transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReceipt() {
        if (cartTextArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Keranjang kosong! Tidak ada yang bisa dicetak.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        JTextArea receiptArea = new JTextArea(20, 30);
        receiptArea.setText("======= STRUK PEMBELIAN =======\n");
        receiptArea.append(cartTextArea.getText());
        receiptArea.append("\nTotal Harga: Rp " + totalHarga);
        receiptArea.append("\n===============================");
    
        JOptionPane.showMessageDialog(null, new JScrollPane(receiptArea), "Struk Pembelian", JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180)); // Warna biru gelap
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }
}