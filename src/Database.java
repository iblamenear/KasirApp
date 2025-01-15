import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/cashier";
    private static final String USER = "root";
    private static final String PASSWORD = "Valdi@#2805**";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    // Metode opsional untuk inisialisasi tabel (hanya jika diperlukan)
    public static void initializeIfNeeded() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();

            // Cek tabel 'users'
            ResultSet usersTable = dbMetaData.getTables(null, null, "users", null);
            if (!usersTable.next()) {
                String createUserTable = "CREATE TABLE users (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "username VARCHAR(50) UNIQUE," +
                        "password VARCHAR(50)" +
                        ")";
                stmt.execute(createUserTable);
            }

            // Cek tabel 'products'
            ResultSet productsTable = dbMetaData.getTables(null, null, "products", null);
            if (!productsTable.next()) {
                String createProductTable = "CREATE TABLE products (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(50)," +
                        "price DOUBLE," +
                        "stock INT" +
                        ")";
                stmt.execute(createProductTable);
            }

            // Cek tabel 'transactions'
            ResultSet transactionsTable = dbMetaData.getTables(null, null, "transactions", null);
            if (!transactionsTable.next()) {
                String createTransactionTable = "CREATE TABLE transactions (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "product_id INT," +
                        "quantity INT," +
                        "total DOUBLE," +
                        "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (product_id) REFERENCES products(id)" +
                        ")";
                stmt.execute(createTransactionTable);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify or initialize database tables", e);
        }
    }

    public static boolean checkUserCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
