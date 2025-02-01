-- Membuat database
CREATE DATABASE cashier;
USE cashier;
drop database cashier;

-- Tabel pengguna (users) untuk login
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Tabel produk (products) untuk menyimpan data barang
CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    stock INT NOT NULL
);
ALTER TABLE products ADD COLUMN category VARCHAR(20);

-- Tabel transaksi (transactions) untuk mencatat pembelian
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    quantity INT NOT NULL,
    total DOUBLE NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Data awal untuk tabel users (Login admin default)
INSERT INTO users (username, password) VALUES ('admin', 'admin123');

-- Data awal untuk tabel products (Contoh produk)
INSERT INTO products (name, price, stock, category) 
VALUES ('Kelapa Sawit', 8000, 100, "BahanMentah"), 
       ('Tebu', 5000, 200, "BahanMentah"), 
       ('Biji Kopi', 3000, 150, "Biji"), 
       ('Biji Kakao', 10000, 120, "Biji"),
       ('Cengkeh', 8000,100, "BahanMentah"),
       ('Pisang',20000,100, "BahanMentah");
       
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(50) NOT NULL,
    attendance_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(50) NOT NULL
);

INSERT INTO staff (employee_code, name, position) VALUES
('MR01', 'Nafilah', 'Kasir'),
('MR02', 'Thifaal', 'Kasir'),
('MR03', 'Justin', 'Kasir'),
('MR04', 'Valdi', 'Kasir');

