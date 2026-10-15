package com.example.warehouse.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes and upgrades MySQL data to the full Indian multi-warehouse catalog:
 * - Indian Fulfillment Hubs (Mumbai, Bengaluru, Delhi NCR, Hyderabad, Chennai, Kolkata)
 * - Indian verified sellers (Appario Retail, Cloudtail India, Tata Croma, Reliance Digital, etc.)
 * - 35 diversified products across electronics, appliances, peripherals, smart home, and gaming
 *   priced realistically in Indian Rupees (₹).
 */
@Component
@Order(1)
public class SqlDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SqlDataInitializer.class);
    private final JdbcTemplate jdbc;

    public SqlDataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        migrateCatalog();
    }

    public synchronized void migrateCatalog() {
        try {
            log.info("Starting Indian catalog and multi-warehouse migration in MySQL...");

            // 1. Ensure tables exist
            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS warehouse (
                    warehouse_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name          VARCHAR(100) NOT NULL,
                    location      VARCHAR(200) NOT NULL
                ) ENGINE=InnoDB;
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS supplier (
                    supplier_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name          VARCHAR(100) NOT NULL,
                    contact_email VARCHAR(150),
                    country       VARCHAR(80)
                ) ENGINE=InnoDB;
            """);

            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS inventory (
                    warehouse_id  BIGINT NOT NULL,
                    prod_id       VARCHAR(50) NOT NULL,
                    prod_name     VARCHAR(200) NOT NULL,
                    stock_number  INT NOT NULL DEFAULT 0,
                    selling_price DECIMAL(10, 2) NOT NULL,
                    supplier_id   BIGINT,
                    category      VARCHAR(100),
                    PRIMARY KEY   (warehouse_id, prod_id),
                    INDEX         idx_category (category),
                    INDEX         idx_prod_id (prod_id)
                ) ENGINE=InnoDB;
            """);

            // 2. Upsert Indian Warehouses
            upsertWarehouse(1L, "Mumbai Fulfillment Center", "Bhiwandi, Maharashtra");
            upsertWarehouse(2L, "Bengaluru Logistics Park", "Whitefield, Bengaluru, Karnataka");
            upsertWarehouse(3L, "Delhi NCR Mega Depot", "Gurugram, Haryana");
            upsertWarehouse(4L, "Hyderabad Tech Hub", "Shamshabad, Telangana");
            upsertWarehouse(5L, "Chennai Coastal Depot", "Sriperumbudur, Tamil Nadu");
            upsertWarehouse(6L, "Kolkata Eastern Hub", "Dankuni, West Bengal");

            // 3. Upsert Indian Suppliers
            upsertSupplier(1L, "Appario Retail India", "support@appario.in", "India");
            upsertSupplier(2L, "Cloudtail India Ltd", "orders@cloudtail.in", "India");
            upsertSupplier(3L, "Tata Croma Distribution", "b2b@croma.com", "India");
            upsertSupplier(4L, "Reliance Digital Logistics", "supply@reliancedigital.in", "India");
            upsertSupplier(5L, "Bharat Electronics Supplies", "sales@bel-supplies.in", "India");
            upsertSupplier(6L, "V-Guard & Havells Hub", "partner@havells.in", "India");

            // 4. Clean old USD prices if present (PROD-001 with price < 10000 was old USD $1299.99)
            jdbc.update("DELETE FROM inventory WHERE prod_id = 'PROD-001' AND selling_price < 10000");

            // 5. Upsert 35 Products in INR across Indian Warehouses
            List<Object[]> items = List.of(
                // ─── Laptops & Computers ───
                item(1L, "PROD-001", "UltraBook Pro 15 OLED", 25, 119999.00, 1L, "Laptops & Computers"),
                item(2L, "PROD-001", "UltraBook Pro 15 OLED", 18, 119999.00, 1L, "Laptops & Computers"),
                item(2L, "PROD-011", "ASUS ROG Zephyrus G16 Gaming Laptop", 25, 164990.00, 1L, "Laptops & Computers"),
                item(4L, "PROD-011", "ASUS ROG Zephyrus G16 Gaming Laptop", 15, 164990.00, 1L, "Laptops & Computers"),
                item(1L, "PROD-012", "Apple MacBook Air 15\" M3 Chip", 35, 134900.00, 3L, "Laptops & Computers"),
                item(3L, "PROD-012", "Apple MacBook Air 15\" M3 Chip", 20, 134900.00, 3L, "Laptops & Computers"),
                item(3L, "PROD-013", "Lenovo ThinkPad X1 Carbon Gen 12", 18, 142500.00, 2L, "Laptops & Computers"),
                item(5L, "PROD-013", "Lenovo ThinkPad X1 Carbon Gen 12", 12, 142500.00, 2L, "Laptops & Computers"),

                // ─── Mobiles & Tablets ───
                item(1L, "PROD-014", "Apple iPhone 16 Pro Max 256GB Titanium", 50, 144900.00, 3L, "Mobiles & Tablets"),
                item(2L, "PROD-014", "Apple iPhone 16 Pro Max 256GB Titanium", 30, 144900.00, 3L, "Mobiles & Tablets"),
                item(3L, "PROD-014", "Apple iPhone 16 Pro Max 256GB Titanium", 25, 144900.00, 3L, "Mobiles & Tablets"),
                item(2L, "PROD-015", "Samsung Galaxy S24 Ultra 5G AI", 40, 129999.00, 4L, "Mobiles & Tablets"),
                item(4L, "PROD-015", "Samsung Galaxy S24 Ultra 5G AI", 22, 129999.00, 4L, "Mobiles & Tablets"),
                item(1L, "PROD-016", "OnePlus 12 5G Flowy Emerald", 65, 64999.00, 1L, "Mobiles & Tablets"),
                item(3L, "PROD-016", "OnePlus 12 5G Flowy Emerald", 35, 64999.00, 1L, "Mobiles & Tablets"),
                item(2L, "PROD-017", "Apple iPad Air 11\" M2 Liquid Retina", 30, 59900.00, 3L, "Mobiles & Tablets"),
                item(5L, "PROD-017", "Apple iPad Air 11\" M2 Liquid Retina", 15, 59900.00, 3L, "Mobiles & Tablets"),

                // ─── Audio & Sound ───
                item(1L, "PROD-002", "Sony WH-1000XM5 ANC Headphones", 60, 26990.00, 2L, "Audio & Sound"),
                item(3L, "PROD-002", "Sony WH-1000XM5 ANC Headphones", 40, 26990.00, 2L, "Audio & Sound"),
                item(1L, "PROD-018", "Apple AirPods Pro (2nd Gen) USB-C", 80, 24900.00, 3L, "Audio & Sound"),
                item(2L, "PROD-018", "Apple AirPods Pro (2nd Gen) USB-C", 50, 24900.00, 3L, "Audio & Sound"),
                item(3L, "PROD-019", "Marshall Stanmore III Bluetooth Speaker", 20, 31999.00, 1L, "Audio & Sound"),
                item(6L, "PROD-019", "Marshall Stanmore III Bluetooth Speaker", 14, 31999.00, 1L, "Audio & Sound"),
                item(4L, "PROD-020", "JBL Cinema SB271 Dolby Digital Soundbar", 45, 11999.00, 4L, "Audio & Sound"),
                item(5L, "PROD-020", "JBL Cinema SB271 Dolby Digital Soundbar", 30, 11999.00, 4L, "Audio & Sound"),

                // ─── Televisions ───
                item(1L, "PROD-003", "Sony Bravia XR 55\" 4K OLED Google TV", 15, 114990.00, 3L, "Televisions"),
                item(2L, "PROD-003", "Sony Bravia XR 55\" 4K OLED Google TV", 10, 114990.00, 3L, "Televisions"),
                item(1L, "PROD-021", "LG C3 65\" 4K OLED evo Smart TV", 12, 159990.00, 4L, "Televisions"),
                item(3L, "PROD-021", "LG C3 65\" 4K OLED evo Smart TV", 8, 159990.00, 4L, "Televisions"),
                item(5L, "PROD-022", "Samsung Crystal 4K Vivid Pro 43\" TV", 50, 28990.00, 2L, "Televisions"),
                item(6L, "PROD-022", "Samsung Crystal 4K Vivid Pro 43\" TV", 35, 28990.00, 2L, "Televisions"),

                // ─── Smart Wearables ───
                item(1L, "PROD-023", "Apple Watch Series 10 GPS 46mm", 35, 49900.00, 3L, "Smart Wearables"),
                item(2L, "PROD-023", "Apple Watch Series 10 GPS 46mm", 25, 49900.00, 3L, "Smart Wearables"),
                item(3L, "PROD-024", "Samsung Galaxy Watch Ultra 47mm LTE", 20, 59999.00, 4L, "Smart Wearables"),
                item(4L, "PROD-024", "Samsung Galaxy Watch Ultra 47mm LTE", 15, 59999.00, 4L, "Smart Wearables"),
                item(2L, "PROD-025", "Garmin Forerunner 265 AMOLED Running Watch", 15, 46490.00, 5L, "Smart Wearables"),
                item(5L, "PROD-025", "Garmin Forerunner 265 AMOLED Running Watch", 10, 46490.00, 5L, "Smart Wearables"),

                // ─── Cameras & Photography ───
                item(1L, "PROD-026", "Sony Alpha 7 IV Full-Frame Mirrorless Camera", 10, 209990.00, 1L, "Cameras"),
                item(3L, "PROD-026", "Sony Alpha 7 IV Full-Frame Mirrorless Camera", 8, 209990.00, 1L, "Cameras"),
                item(1L, "PROD-027", "DJI Osmo Pocket 3 Creator Combo Gimbal", 28, 53990.00, 2L, "Cameras"),
                item(2L, "PROD-027", "DJI Osmo Pocket 3 Creator Combo Gimbal", 18, 53990.00, 2L, "Cameras"),
                item(4L, "PROD-028", "GoPro HERO13 Black 5.3K Action Camera", 35, 44990.00, 4L, "Cameras"),
                item(6L, "PROD-028", "GoPro HERO13 Black 5.3K Action Camera", 20, 44990.00, 4L, "Cameras"),

                // ─── Gaming & Peripherals ───
                item(1L, "PROD-029", "Sony PlayStation 5 Slim Disc Edition 1TB", 40, 54990.00, 1L, "Gaming"),
                item(2L, "PROD-029", "Sony PlayStation 5 Slim Disc Edition 1TB", 30, 54990.00, 1L, "Gaming"),
                item(3L, "PROD-029", "Sony PlayStation 5 Slim Disc Edition 1TB", 20, 54990.00, 1L, "Gaming"),
                item(2L, "PROD-030", "Nintendo Switch OLED Model Mario Red", 30, 31999.00, 2L, "Gaming"),
                item(5L, "PROD-030", "Nintendo Switch OLED Model Mario Red", 20, 31999.00, 2L, "Gaming"),
                item(2L, "PROD-004", "Mechanical Gaming Keyboard RGB Brown Switch", 120, 7499.00, 5L, "Peripherals"),
                item(3L, "PROD-004", "Mechanical Gaming Keyboard RGB Brown Switch", 80, 7499.00, 5L, "Peripherals"),
                item(1L, "PROD-031", "Logitech G502 X PLUS Wireless RGB Mouse", 70, 13995.00, 1L, "Peripherals"),
                item(4L, "PROD-031", "Logitech G502 X PLUS Wireless RGB Mouse", 45, 13995.00, 1L, "Peripherals"),
                item(2L, "PROD-009", "Logitech Brio 4K Pro Ultra HD Webcam", 55, 9499.00, 5L, "Peripherals"),

                // ─── Home & Kitchen Appliances ───
                item(1L, "PROD-032", "Dyson V12 Detect Slim Cordless Vacuum", 15, 51900.00, 3L, "Home Appliances"),
                item(3L, "PROD-032", "Dyson V12 Detect Slim Cordless Vacuum", 12, 51900.00, 3L, "Home Appliances"),
                item(2L, "PROD-033", "Philips Digital Air Fryer HD9252 4.1L", 60, 8999.00, 6L, "Kitchen Appliances"),
                item(6L, "PROD-033", "Philips Digital Air Fryer HD9252 4.1L", 40, 8999.00, 6L, "Kitchen Appliances"),
                item(1L, "PROD-034", "Coway Airmega 150 HEPA Air Purifier", 45, 13490.00, 5L, "Home Appliances"),
                item(3L, "PROD-034", "Coway Airmega 150 HEPA Air Purifier", 30, 13490.00, 5L, "Home Appliances"),
                item(2L, "PROD-035", "Ecovacs Deebot N10 Robotic Vacuum & Mop", 22, 29900.00, 4L, "Home Appliances"),
                item(4L, "PROD-035", "Ecovacs Deebot N10 Robotic Vacuum & Mop", 18, 29900.00, 4L, "Home Appliances"),

                // ─── Accessories, Storage & Networking ───
                item(1L, "PROD-005", "USB-C Triple Display Docking Station 140W", 45, 9999.00, 1L, "Accessories"),
                item(2L, "PROD-006", "SanDisk Extreme Portable SSD 2TB NVMe", 90, 14999.00, 4L, "Storage"),
                item(3L, "PROD-007", "Smart Home Hub Thread & Matter Multi-Protocol", 30, 7999.00, 2L, "Smart Home"),
                item(1L, "PROD-008", "Featherlite Ergonomic High-Back Mesh Chair", 15, 18499.00, 1L, "Furniture"),
                item(5L, "PROD-008", "Featherlite Ergonomic High-Back Mesh Chair", 12, 18499.00, 1L, "Furniture"),
                item(3L, "PROD-010", "TP-Link Archer AXE75 Wi-Fi 6E Tri-Band Router", 22, 15999.00, 1L, "Networking"),
                item(6L, "PROD-010", "TP-Link Archer AXE75 Wi-Fi 6E Tri-Band Router", 18, 15999.00, 1L, "Networking")
            );

            for (Object[] row : items) {
                jdbc.update("""
                    INSERT INTO inventory (warehouse_id, prod_id, prod_name, stock_number, selling_price, supplier_id, category)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        prod_name = VALUES(prod_name),
                        stock_number = VALUES(stock_number),
                        selling_price = VALUES(selling_price),
                        supplier_id = VALUES(supplier_id),
                        category = VALUES(category)
                """, row);
            }

            log.info("MySQL Indian catalog migration successfully completed ({} inventory entries)!", items.size());
        } catch (Exception e) {
            log.error("Failed to migrate Indian catalog into MySQL: {}", e.getMessage(), e);
        }
    }

    private void upsertWarehouse(Long id, String name, String location) {
        jdbc.update("""
            INSERT INTO warehouse (warehouse_id, name, location)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE name = VALUES(name), location = VALUES(location)
        """, id, name, location);
    }

    private void upsertSupplier(Long id, String name, String email, String country) {
        jdbc.update("""
            INSERT INTO supplier (supplier_id, name, contact_email, country)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE name = VALUES(name), contact_email = VALUES(contact_email), country = VALUES(country)
        """, id, name, email, country);
    }

    private Object[] item(Long wId, String pId, String name, int stock, double price, Long sId, String cat) {
        return new Object[]{ wId, pId, name, stock, price, sId, cat };
    }
}
