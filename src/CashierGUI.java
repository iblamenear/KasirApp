import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class CashierGUI extends JFrame { // Class Cashier GUI
    private JTable productTable; // Atribut untuk Table
    private DefaultTableModel productTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextArea cartTextArea;
    private double totalHarga = 0;

    public CashierGUI() { // Konstruktor CashierGUI
        setTitle("Kasir - Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadProductData();

        setVisible(true);
    }

    private void initUI() { // Method untuk inisialisasi UI
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        // Tab Transaksi
        JPanel transactionPanel = createTransactionPanel();
        tabbedPane.addTab("Transaksi", transactionPanel);

        // Tab Riwayat Transaksi
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("Riwayat", historyPanel);

        // Tab Absensi
        JPanel attendancePanel = createAttendancePanel();
        tabbedPane.addTab("Absensi", attendancePanel);

        add(tabbedPane, BorderLayout.CENTER);

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
        JTabbedPane tabbedPane = new JTabbedPane();
        // Panel Produk dan Transaksi
        tabbedPane.addTab("Produk", splitPane);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // Panel Absensi
        JPanel attendancePanel = createAttendancePanel();
        tabbedPane.addTab("Absensi", attendancePanel);

        JPanel productPanel = createProductPanel();
        splitPane.setLeftComponent(productPanel);

        JPanel rightPanel = createRightPanel();
        splitPane.setRightComponent(rightPanel);

        add(tabbedPane, BorderLayout.CENTER);

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

        JButton uploadFileButton = new JButton("Unggah File Restock");
        styleButton(uploadFileButton); // Jika Anda sudah memiliki metode `styleButton`
        buttonPanel.add(uploadFileButton);

        // Tombol Absensi
        JButton attendanceButton = new JButton("Absensi");
        attendanceButton.setBackground(new Color(0, 122, 255)); // Warna biru
        attendanceButton.setForeground(Color.WHITE); // Warna teks putih
        attendanceButton.setFocusPainted(false); // Hilangkan border fokus
        attendanceButton.setOpaque(true); // Pastikan warna tombol terlihat
        attendanceButton.addActionListener(e -> handleAttendance());
        buttonPanel.add(attendanceButton);
    
        productPanel.add(buttonPanel, BorderLayout.SOUTH);

        uploadFileButton.addActionListener(e -> handleFileRestock());

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

    private void handleFileRestock() {
        // Dialog untuk memilih file
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                Connection conn = Database.connect()) {

                String line;
                int successCount = 0;
                int errorCount = 0;

            while ((line = reader.readLine()) != null) {
                // Pisahkan ID dan jumlah berdasarkan format "ID,Jumlah"
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    errorCount++;
                    continue; // Lewati baris jika format salah
                }

                try {
                    int productId = Integer.parseInt(parts[0].trim());
                    int quantity = Integer.parseInt(parts[1].trim());

                    // Update stok produk di database
                    String updateStockQuery = "UPDATE products SET stock = stock + ? WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(updateStockQuery);
                    stmt.setInt(1, quantity);
                    stmt.setInt(2, productId);
                    int rowsUpdated = stmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        successCount++;
                    } else {
                        errorCount++; // Produk dengan ID tidak ditemukan
                    }
                } catch (NumberFormatException e) {
                    errorCount++; // Jika ID atau jumlah tidak valid
                }
            }

            // Tampilkan hasil proses
            JOptionPane.showMessageDialog(this,
                    "Restock berhasil untuk " + successCount + " produk.\n" +
                            "Baris gagal diproses: " + errorCount,
                    "Hasil Restock",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh tabel produk
            productTableModel.setRowCount(0);
            loadProductData();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Gagal membaca file!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui stok produk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createAttendancePanel() {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder("Absensi Karyawan"));
    
        // Form untuk memasukkan kode karyawan
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Kode Karyawan: "));
        JTextField employeeCodeField = new JTextField(15);
        formPanel.add(employeeCodeField);
        JButton markAttendanceButton = new JButton("Absen");
        formPanel.add(markAttendanceButton);
    
        // Area untuk menampilkan daftar absensi
        JTextArea attendanceLogArea = new JTextArea(15, 30);
        attendanceLogArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(attendanceLogArea);
    
        attendancePanel.add(formPanel, BorderLayout.NORTH);
        attendancePanel.add(logScrollPane, BorderLayout.CENTER);
    
        // Action Listener untuk tombol Absen
        markAttendanceButton.addActionListener(e -> handleAttendanceWithValidation(employeeCodeField, attendanceLogArea));
    
        return attendancePanel;
    }    

    private void handleAttendance() {
        String staffId = JOptionPane.showInputDialog(this, "Masukkan Kode Karyawan:", "Absensi", JOptionPane.PLAIN_MESSAGE);
    
        if (staffId != null && !staffId.trim().isEmpty()) {
            if (markAttendance(staffId.trim())) {
                JOptionPane.showMessageDialog(this, "Absensi berhasil untuk Kode Karyawan: " + staffId, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Kode Karyawan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateAttendanceLog(JTextArea attendanceLogArea) { // Method untuk mengupdate log kehadiran
        attendanceLogArea.setText(""); // Kosongkan area log
    
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.employee_code, s.name, a.attendance_date " +
                 "FROM attendance a " +
                 "JOIN staff s ON a.employee_code = s.employee_code " +
                 "ORDER BY a.attendance_date DESC")) {
    
            while (rs.next()) {
                String employeeCode = rs.getString("employee_code");
                String name = rs.getString("name");
                Timestamp attendanceDate = rs.getTimestamp("attendance_date");
                attendanceLogArea.append(employeeCode + " (" + name + ") - " + attendanceDate + "\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat log absensi!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleAttendanceWithValidation(JTextField employeeCodeField, JTextArea attendanceLogArea) { // Method untuk memastikan kode karyawan valid sebelum mencatat absensi
        String employeeCode = employeeCodeField.getText().trim();
    
        if (employeeCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode karyawan tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try (Connection conn = Database.connect()) {
            // Validasi apakah kode karyawan ada di tabel staff
            String validationQuery = "SELECT name FROM staff WHERE employee_code = ?";
            PreparedStatement validationStmt = conn.prepareStatement(validationQuery);
            validationStmt.setString(1, employeeCode);
            ResultSet rs = validationStmt.executeQuery();
    
            if (rs.next()) {
                // Jika valid, catat absensi
                String insertQuery = "INSERT INTO attendance (employee_code) VALUES (?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, employeeCode);
                insertStmt.executeUpdate();
    
                // Tampilkan pesan sukses
                JOptionPane.showMessageDialog(this, "Absensi berhasil dicatat untuk " + rs.getString("name"), "Sukses", JOptionPane.INFORMATION_MESSAGE);
    
                // Perbarui log absensi
                updateAttendanceLog(attendanceLogArea);
    
                // Kosongkan field kode karyawan
                employeeCodeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Kode karyawan tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencatat absensi!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean markAttendance(String employeeCode) {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO attendance (employee_code, attendance_date) " +
                     "SELECT employee_code, NOW() FROM staff WHERE employee_code = ?")) {
    
            stmt.setString(1, employeeCode); // Mengisi parameter employee_code
            int rows = stmt.executeUpdate(); // Menjalankan query
            return rows > 0; // True jika absensi berhasil
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencatat absensi!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
}