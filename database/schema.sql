-- ============================================================
-- warehouse_db Schema — MySQL (Indian Multi-Warehouse E-Commerce)
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

-- Default admin (password: Admin@123 — BCrypt hash)
INSERT IGNORE INTO users (username, password_hash, role)
VALUES ('admin', '$2a$12$Axl5Z6CsdmDUPfla09yWaOgnsDGR6wV1m80/nzzBkeG0SSqpBf48K', 'ADMIN');

-- ─── Indian Warehouses ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS warehouse (
    warehouse_id  BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    location      VARCHAR(200)  NOT NULL
) ENGINE=InnoDB;

INSERT INTO warehouse (warehouse_id, name, location) VALUES
(1, 'Mumbai Fulfillment Center', 'Bhiwandi, Maharashtra'),
(2, 'Bengaluru Logistics Park',   'Whitefield, Bengaluru, Karnataka'),
(3, 'Delhi NCR Mega Depot',       'Gurugram, Haryana'),
(4, 'Hyderabad Tech Hub',         'Shamshabad, Telangana'),
(5, 'Chennai Coastal Depot',       'Sriperumbudur, Tamil Nadu'),
(6, 'Kolkata Eastern Hub',        'Dankuni, West Bengal')
ON DUPLICATE KEY UPDATE name = VALUES(name), location = VALUES(location);

-- ─── Indian Verified Suppliers / Sellers ─────────────────────
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id   BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    contact_email VARCHAR(150),
    country       VARCHAR(80)
) ENGINE=InnoDB;

INSERT INTO supplier (supplier_id, name, contact_email, country) VALUES
(1, 'Appario Retail India',       'support@appario.in',     'India'),
(2, 'Cloudtail India Ltd',        'orders@cloudtail.in',    'India'),
(3, 'Tata Croma Distribution',   'b2b@croma.com',          'India'),
(4, 'Reliance Digital Logistics', 'supply@reliancedigital.in', 'India'),
(5, 'Bharat Electronics Supplies','sales@bel-supplies.in',  'India'),
(6, 'V-Guard & Havells Hub',      'partner@havells.in',     'India')
ON DUPLICATE KEY UPDATE name = VALUES(name), contact_email = VALUES(contact_email), country = VALUES(country);

-- ─── Inventory (Prices in Indian Rupees ₹) ───────────────────
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

INSERT INTO inventory (warehouse_id, prod_id, prod_name, stock_number, selling_price, supplier_id, category) VALUES
-- Laptops & Computers
(1, 'PROD-001', 'UltraBook Pro 15 OLED', 25, 119999.00, 1, 'Laptops & Computers'),
(2, 'PROD-001', 'UltraBook Pro 15 OLED', 18, 119999.00, 1, 'Laptops & Computers'),
(2, 'PROD-011', 'ASUS ROG Zephyrus G16 Gaming Laptop', 25, 164990.00, 1, 'Laptops & Computers'),
(4, 'PROD-011', 'ASUS ROG Zephyrus G16 Gaming Laptop', 15, 164990.00, 1, 'Laptops & Computers'),
(1, 'PROD-012', 'Apple MacBook Air 15" M3 Chip', 35, 134900.00, 3, 'Laptops & Computers'),
(3, 'PROD-012', 'Apple MacBook Air 15" M3 Chip', 20, 134900.00, 3, 'Laptops & Computers'),
(3, 'PROD-013', 'Lenovo ThinkPad X1 Carbon Gen 12', 18, 142500.00, 2, 'Laptops & Computers'),
(5, 'PROD-013', 'Lenovo ThinkPad X1 Carbon Gen 12', 12, 142500.00, 2, 'Laptops & Computers'),

-- Mobiles & Tablets
(1, 'PROD-014', 'Apple iPhone 16 Pro Max 256GB Titanium', 50, 144900.00, 3, 'Mobiles & Tablets'),
(2, 'PROD-014', 'Apple iPhone 16 Pro Max 256GB Titanium', 30, 144900.00, 3, 'Mobiles & Tablets'),
(3, 'PROD-014', 'Apple iPhone 16 Pro Max 256GB Titanium', 25, 144900.00, 3, 'Mobiles & Tablets'),
(2, 'PROD-015', 'Samsung Galaxy S24 Ultra 5G AI', 40, 129999.00, 4, 'Mobiles & Tablets'),
(4, 'PROD-015', 'Samsung Galaxy S24 Ultra 5G AI', 22, 129999.00, 4, 'Mobiles & Tablets'),
(1, 'PROD-016', 'OnePlus 12 5G Flowy Emerald', 65, 64999.00, 1, 'Mobiles & Tablets'),
(3, 'PROD-016', 'OnePlus 12 5G Flowy Emerald', 35, 64999.00, 1, 'Mobiles & Tablets'),
(2, 'PROD-017', 'Apple iPad Air 11" M2 Liquid Retina', 30, 59900.00, 3, 'Mobiles & Tablets'),
(5, 'PROD-017', 'Apple iPad Air 11" M2 Liquid Retina', 15, 59900.00, 3, 'Mobiles & Tablets'),

-- Audio & Sound
(1, 'PROD-002', 'Sony WH-1000XM5 ANC Headphones', 60, 26990.00, 2, 'Audio & Sound'),
(3, 'PROD-002', 'Sony WH-1000XM5 ANC Headphones', 40, 26990.00, 2, 'Audio & Sound'),
(1, 'PROD-018', 'Apple AirPods Pro (2nd Gen) USB-C', 80, 24900.00, 3, 'Audio & Sound'),
(2, 'PROD-018', 'Apple AirPods Pro (2nd Gen) USB-C', 50, 24900.00, 3, 'Audio & Sound'),
(3, 'PROD-019', 'Marshall Stanmore III Bluetooth Speaker', 20, 31999.00, 1, 'Audio & Sound'),
(6, 'PROD-019', 'Marshall Stanmore III Bluetooth Speaker', 14, 31999.00, 1, 'Audio & Sound'),
(4, 'PROD-020', 'JBL Cinema SB271 Dolby Digital Soundbar', 45, 11999.00, 4, 'Audio & Sound'),
(5, 'PROD-020', 'JBL Cinema SB271 Dolby Digital Soundbar', 30, 11999.00, 4, 'Audio & Sound'),

-- Televisions
(1, 'PROD-003', 'Sony Bravia XR 55" 4K OLED Google TV', 15, 114990.00, 3, 'Televisions'),
(2, 'PROD-003', 'Sony Bravia XR 55" 4K OLED Google TV', 10, 114990.00, 3, 'Televisions'),
(1, 'PROD-021', 'LG C3 65" 4K OLED evo Smart TV', 12, 159990.00, 4, 'Televisions'),
(3, 'PROD-021', 'LG C3 65" 4K OLED evo Smart TV', 8, 159990.00, 4, 'Televisions'),
(5, 'PROD-022', 'Samsung Crystal 4K Vivid Pro 43" TV', 50, 28990.00, 2, 'Televisions'),
(6, 'PROD-022', 'Samsung Crystal 4K Vivid Pro 43" TV', 35, 28990.00, 2, 'Televisions'),

-- Smart Wearables
(1, 'PROD-023', 'Apple Watch Series 10 GPS 46mm', 35, 49900.00, 3, 'Smart Wearables'),
(2, 'PROD-023', 'Apple Watch Series 10 GPS 46mm', 25, 49900.00, 3, 'Smart Wearables'),
(3, 'PROD-024', 'Samsung Galaxy Watch Ultra 47mm LTE', 20, 59999.00, 4, 'Smart Wearables'),
(4, 'PROD-024', 'Samsung Galaxy Watch Ultra 47mm LTE', 15, 59999.00, 4, 'Smart Wearables'),
(2, 'PROD-025', 'Garmin Forerunner 265 AMOLED Running Watch', 15, 46490.00, 5, 'Smart Wearables'),
(5, 'PROD-025', 'Garmin Forerunner 265 AMOLED Running Watch', 10, 46490.00, 5, 'Smart Wearables'),

-- Cameras & Photography
(1, 'PROD-026', 'Sony Alpha 7 IV Full-Frame Mirrorless Camera', 10, 209990.00, 1, 'Cameras'),
(3, 'PROD-026', 'Sony Alpha 7 IV Full-Frame Mirrorless Camera', 8, 209990.00, 1, 'Cameras'),
(1, 'PROD-027', 'DJI Osmo Pocket 3 Creator Combo Gimbal', 28, 53990.00, 2, 'Cameras'),
(2, 'PROD-027', 'DJI Osmo Pocket 3 Creator Combo Gimbal', 18, 53990.00, 2, 'Cameras'),
(4, 'PROD-028', 'GoPro HERO13 Black 5.3K Action Camera', 35, 44990.00, 4, 'Cameras'),
(6, 'PROD-028', 'GoPro HERO13 Black 5.3K Action Camera', 20, 44990.00, 4, 'Cameras'),

-- Gaming & Peripherals
(1, 'PROD-029', 'Sony PlayStation 5 Slim Disc Edition 1TB', 40, 54990.00, 1, 'Gaming'),
(2, 'PROD-029', 'Sony PlayStation 5 Slim Disc Edition 1TB', 30, 54990.00, 1, 'Gaming'),
(3, 'PROD-029', 'Sony PlayStation 5 Slim Disc Edition 1TB', 20, 54990.00, 1, 'Gaming'),
(2, 'PROD-030', 'Nintendo Switch OLED Model Mario Red', 30, 31999.00, 2, 'Gaming'),
(5, 'PROD-030', 'Nintendo Switch OLED Model Mario Red', 20, 31999.00, 2, 'Gaming'),
(2, 'PROD-004', 'Mechanical Gaming Keyboard RGB Brown Switch', 120, 7499.00, 5, 'Peripherals'),
(3, 'PROD-004', 'Mechanical Gaming Keyboard RGB Brown Switch', 80, 7499.00, 5, 'Peripherals'),
(1, 'PROD-031', 'Logitech G502 X PLUS Wireless RGB Mouse', 70, 13995.00, 1, 'Peripherals'),
(4, 'PROD-031', 'Logitech G502 X PLUS Wireless RGB Mouse', 45, 13995.00, 1, 'Peripherals'),
(2, 'PROD-009', 'Logitech Brio 4K Pro Ultra HD Webcam', 55, 9499.00, 5, 'Peripherals'),

-- Home & Kitchen Appliances
(1, 'PROD-032', 'Dyson V12 Detect Slim Cordless Vacuum', 15, 51900.00, 3, 'Home Appliances'),
(3, 'PROD-032', 'Dyson V12 Detect Slim Cordless Vacuum', 12, 51900.00, 3, 'Home Appliances'),
(2, 'PROD-033', 'Philips Digital Air Fryer HD9252 4.1L', 60, 8999.00, 6, 'Kitchen Appliances'),
(6, 'PROD-033', 'Philips Digital Air Fryer HD9252 4.1L', 40, 8999.00, 6, 'Kitchen Appliances'),
(1, 'PROD-034', 'Coway Airmega 150 HEPA Air Purifier', 45, 13490.00, 5, 'Home Appliances'),
(3, 'PROD-034', 'Coway Airmega 150 HEPA Air Purifier', 30, 13490.00, 5, 'Home Appliances'),
(2, 'PROD-035', 'Ecovacs Deebot N10 Robotic Vacuum & Mop', 22, 29900.00, 4, 'Home Appliances'),
(4, 'PROD-035', 'Ecovacs Deebot N10 Robotic Vacuum & Mop', 18, 29900.00, 4, 'Home Appliances'),

-- Accessories, Storage & Networking
(1, 'PROD-005', 'USB-C Triple Display Docking Station 140W', 45, 9999.00, 1, 'Accessories'),
(2, 'PROD-006', 'SanDisk Extreme Portable SSD 2TB NVMe', 90, 14999.00, 4, 'Storage'),
(3, 'PROD-007', 'Smart Home Hub Thread & Matter Multi-Protocol', 30, 7999.00, 2, 'Smart Home'),
(1, 'PROD-008', 'Featherlite Ergonomic High-Back Mesh Chair', 15, 18499.00, 1, 'Furniture'),
(5, 'PROD-008', 'Featherlite Ergonomic High-Back Mesh Chair', 12, 18499.00, 1, 'Furniture'),
(3, 'PROD-010', 'TP-Link Archer AXE75 Wi-Fi 6E Tri-Band Router', 22, 15999.00, 1, 'Networking'),
(6, 'PROD-010', 'TP-Link Archer AXE75 Wi-Fi 6E Tri-Band Router', 18, 15999.00, 1, 'Networking')
ON DUPLICATE KEY UPDATE
    prod_name = VALUES(prod_name),
    stock_number = VALUES(stock_number),
    selling_price = VALUES(selling_price),
    supplier_id = VALUES(supplier_id),
    category = VALUES(category);
