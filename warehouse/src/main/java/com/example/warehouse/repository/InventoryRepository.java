package com.example.warehouse.repository;

import com.example.warehouse.model.Inventory;
import com.example.warehouse.model.Supplier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data-access layer for the MySQL {@code inventory} and {@code supplier} tables.
 * All queries use {@link JdbcTemplate} with parameterized statements to prevent SQL injection.
 */
@Repository
public class InventoryRepository {

    private final JdbcTemplate jdbc;

    public InventoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ─── RowMappers ──────────────────────────────────────────────────────────

    private static final RowMapper<Inventory> INVENTORY_ROW_MAPPER = (rs, rowNum) -> {
        Inventory inv = new Inventory();
        inv.setWarehouseId(rs.getLong("warehouse_id"));
        inv.setProdId(rs.getString("prod_id"));
        inv.setProdName(rs.getString("prod_name"));
        inv.setStockNumber(rs.getInt("stock_number"));
        inv.setSellingPrice(rs.getDouble("selling_price"));
        inv.setSupplierId(rs.getLong("supplier_id"));
        inv.setCategory(rs.getString("category"));
        // supplier_name may be null if not joined
        try { inv.setSupplierName(rs.getString("supplier_name")); } catch (SQLException ignored) {}
        return inv;
    };

    private static final RowMapper<Supplier> SUPPLIER_ROW_MAPPER = (rs, rowNum) -> {
        Supplier s = new Supplier();
        s.setSupplierId(rs.getLong("supplier_id"));
        s.setName(rs.getString("name"));
        try { s.setContactEmail(rs.getString("contact_email")); } catch (SQLException ignored) {}
        try { s.setCountry(rs.getString("country")); } catch (SQLException ignored) {}
        return s;
    };

    // ─── Reads ───────────────────────────────────────────────────────────────

    /**
     * Fetches all inventory rows joined with supplier names.
     */
    public List<Inventory> findAll() {
        String sql = """
                SELECT i.*, s.name AS supplier_name
                FROM inventory i
                LEFT JOIN supplier s ON i.supplier_id = s.supplier_id
                ORDER BY i.prod_id, i.warehouse_id
                """;
        return jdbc.query(sql, INVENTORY_ROW_MAPPER);
    }

    /**
     * Fetches all rows for a given product across every warehouse.
     */
    public List<Inventory> findByProdId(String prodId) {
        String sql = """
                SELECT i.*, s.name AS supplier_name
                FROM inventory i
                LEFT JOIN supplier s ON i.supplier_id = s.supplier_id
                WHERE i.prod_id = ?
                """;
        return jdbc.query(sql, INVENTORY_ROW_MAPPER, prodId);
    }

    /**
     * Fetches a single inventory record by composite PK.
     */
    public Optional<Inventory> findByWarehouseAndProdId(Long warehouseId, String prodId) {
        String sql = """
                SELECT i.*, s.name AS supplier_name
                FROM inventory i
                LEFT JOIN supplier s ON i.supplier_id = s.supplier_id
                WHERE i.warehouse_id = ? AND i.prod_id = ?
                """;
        List<Inventory> results = jdbc.query(sql, INVENTORY_ROW_MAPPER, warehouseId, prodId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Fetches all inventory for a category (case-insensitive).
     */
    public List<Inventory> findByCategory(String category) {
        String sql = """
                SELECT i.*, s.name AS supplier_name
                FROM inventory i
                LEFT JOIN supplier s ON i.supplier_id = s.supplier_id
                WHERE LOWER(i.category) = LOWER(?)
                ORDER BY i.prod_id
                """;
        return jdbc.query(sql, INVENTORY_ROW_MAPPER, category);
    }

    /**
     * Full-text style search on prod_name (LIKE pattern).
     */
    public List<Inventory> search(String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        String sql = """
                SELECT i.*, s.name AS supplier_name
                FROM inventory i
                LEFT JOIN supplier s ON i.supplier_id = s.supplier_id
                WHERE LOWER(i.prod_name) LIKE ? OR LOWER(i.category) LIKE ?
                ORDER BY i.prod_id
                """;
        return jdbc.query(sql, INVENTORY_ROW_MAPPER, pattern, pattern);
    }

    // ─── Writes ──────────────────────────────────────────────────────────────

    /**
     * Inserts a new inventory row. Uses INSERT IGNORE on composite PK to be idempotent.
     */
    public Inventory save(Inventory inv) {
        String sql = """
                INSERT INTO inventory
                    (warehouse_id, prod_id, prod_name, stock_number, selling_price, supplier_id, category)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbc.update(sql,
                inv.getWarehouseId(),
                inv.getProdId(),
                inv.getProdName(),
                inv.getStockNumber(),
                inv.getSellingPrice(),
                inv.getSupplierId(),
                inv.getCategory());
        return inv;
    }

    /**
     * Partial update: uses COALESCE so NULL/blank values leave existing data intact.
     */
    public void update(Inventory inv) {
        String sql = """
                UPDATE inventory SET
                    prod_name     = COALESCE(NULLIF(?, ''), prod_name),
                    stock_number  = COALESCE(?, stock_number),
                    selling_price = COALESCE(?, selling_price),
                    supplier_id   = COALESCE(?, supplier_id),
                    category      = COALESCE(NULLIF(?, ''), category)
                WHERE warehouse_id = ? AND prod_id = ?
                """;
        jdbc.update(sql,
                inv.getProdName(),
                inv.getStockNumber(),
                inv.getSellingPrice(),
                inv.getSupplierId(),
                inv.getCategory(),
                inv.getWarehouseId(),
                inv.getProdId());
    }

    /**
     * Atomically decrements stock by 1, guarded by {@code stock_number > 0}.
     * Returns the number of rows affected (0 means out-of-stock).
     */
    public int decrementStock(Long warehouseId, String prodId) {
        String sql = """
                UPDATE inventory
                SET stock_number = stock_number - 1
                WHERE warehouse_id = ? AND prod_id = ? AND stock_number > 0
                """;
        return jdbc.update(sql, warehouseId, prodId);
    }

    /**
     * Increments stock by 1 (used when removing an item from cart).
     */
    public void incrementStock(Long warehouseId, String prodId) {
        String sql = """
                UPDATE inventory
                SET stock_number = stock_number + 1
                WHERE warehouse_id = ? AND prod_id = ?
                """;
        jdbc.update(sql, warehouseId, prodId);
    }

    /**
     * Deletes a single warehouse-product record.
     */
    public void deleteByWarehouseAndProdId(Long warehouseId, String prodId) {
        jdbc.update("DELETE FROM inventory WHERE warehouse_id = ? AND prod_id = ?",
                warehouseId, prodId);
    }

    // ─── Suppliers ───────────────────────────────────────────────────────────

    public List<Supplier> findAllSuppliers() {
        return jdbc.query("SELECT * FROM supplier ORDER BY name", SUPPLIER_ROW_MAPPER);
    }

    // ─── Warehouses ──────────────────────────────────────────────────────────

    public List<com.example.warehouse.model.Warehouse> findAllWarehouses() {
        return jdbc.query("SELECT * FROM warehouse ORDER BY name",
                (rs, rowNum) -> {
                    com.example.warehouse.model.Warehouse w = new com.example.warehouse.model.Warehouse();
                    w.setWarehouseId(rs.getLong("warehouse_id"));
                    w.setName(rs.getString("name"));
                    w.setLocation(rs.getString("location"));
                    return w;
                });
    }
}
