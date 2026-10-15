-- ============================================================
-- warehouse_db Schema — MySQL
-- Run this once to bootstrap the database.
-- ============================================================

CREATE DATABASE IF NOT EXISTS warehouse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE warehouse_db;

-- ─── Users ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)   NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'CUSTOMER',
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Seed admin (password: Admin@123 — BCrypt hash)
INSERT IGNORE INTO users (username, password_hash, role)
VALUES ('admin', '$2a$12$Axl5Z6CsdmDUPfla09yWaOgnsDGR6wV1m80/nzzBkeG0SSqpBf48K', 'ADMIN');

-- ─── Warehouse ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS warehouse (
    warehouse_id  BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    location      VARCHAR(200)  NOT NULL
) ENGINE=InnoDB;

INSERT IGNORE INTO warehouse (warehouse_id, name, location) VALUES
(1, 'East Coast Hub',  'Newark, NJ'),
(2, 'West Coast Hub',  'Los Angeles, CA'),
(3, 'Central Depot',   'Chicago, IL');

-- ─── Supplier ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id   BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    contact_email VARCHAR(150),
    country       VARCHAR(80)
) ENGINE=InnoDB;

INSERT IGNORE INTO supplier (supplier_id, name, contact_email, country) VALUES
(1, 'TechNova Inc.',     'orders@technova.com',  'USA'),
(2, 'GlobalGadgets Ltd', 'supply@ggadgets.co',   'Germany'),
(3, 'SwiftSource Corp',  'biz@swiftsource.io',   'Taiwan');

-- ─── Inventory ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS inventory (
    warehouse_id  BIGINT          NOT NULL,
    prod_id       VARCHAR(50)     NOT NULL,
    prod_name     VARCHAR(200)    NOT NULL,
    stock_number  INT             NOT NULL DEFAULT 0 CHECK (stock_number >= 0),
    selling_price DECIMAL(10, 2)  NOT NULL,
    supplier_id   BIGINT,
    category      VARCHAR(100),
    PRIMARY KEY   (warehouse_id, prod_id),
    FOREIGN KEY   (warehouse_id) REFERENCES warehouse(warehouse_id) ON DELETE CASCADE,
    FOREIGN KEY   (supplier_id)  REFERENCES supplier(supplier_id)   ON DELETE SET NULL,
    INDEX         idx_category   (category),
    INDEX         idx_prod_id    (prod_id)
) ENGINE=InnoDB;

-- Seed inventory across warehouses
INSERT IGNORE INTO inventory (warehouse_id, prod_id, prod_name, stock_number, selling_price, supplier_id, category) VALUES
(1, 'PROD-001', 'UltraBook Pro 15',      25,  1299.99, 1, 'Laptops'),
(2, 'PROD-001', 'UltraBook Pro 15',      18,  1299.99, 1, 'Laptops'),
(1, 'PROD-002', 'Wireless Noise-Cancel Headphones', 60, 249.99, 2, 'Audio'),
(3, 'PROD-002', 'Wireless Noise-Cancel Headphones', 40, 249.99, 2, 'Audio'),
(1, 'PROD-003', '4K Smart TV 55"',        8,  799.99,  3, 'Televisions'),
(2, 'PROD-004', 'Mechanical Keyboard RGB', 120, 89.99, 2, 'Peripherals'),
(3, 'PROD-004', 'Mechanical Keyboard RGB', 80,  89.99, 2, 'Peripherals'),
(1, 'PROD-005', 'USB-C Docking Station',  45,  149.99, 1, 'Accessories'),
(2, 'PROD-006', 'Portable SSD 2TB',       90,  179.99, 3, 'Storage'),
(3, 'PROD-007', 'Smart Home Hub',         30,   99.99, 2, 'Smart Home'),
(1, 'PROD-008', 'Ergonomic Office Chair', 15,  449.99, 1, 'Furniture'),
(2, 'PROD-009', 'Webcam 4K Pro',          55,   119.99, 3, 'Peripherals'),
(3, 'PROD-010', 'Wi-Fi 6E Router',        22,   199.99, 1, 'Networking');
