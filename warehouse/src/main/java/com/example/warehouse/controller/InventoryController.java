package com.example.warehouse.controller;

import com.example.warehouse.dto.CheckoutRequest;
import com.example.warehouse.dto.FullProductDTO;
import com.example.warehouse.dto.GroupedProductDTO;
import com.example.warehouse.model.Inventory;
import com.example.warehouse.model.ProductDetails;
import com.example.warehouse.repository.InventoryRepository;
import com.example.warehouse.repository.ProductDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Core e-commerce catalog, cart, and admin inventory endpoints.
 *
 * <p>Read endpoints are public; mutations are role-gated in {@code SecurityConfig}.</p>
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepo;
    private final ProductDetailsRepository mongoRepo;
    private final com.example.warehouse.config.MongoDataInitializer mongoSeeder;

    public InventoryController(InventoryRepository inventoryRepo,
                               ProductDetailsRepository mongoRepo,
                               com.example.warehouse.config.MongoDataInitializer mongoSeeder) {
        this.inventoryRepo = inventoryRepo;
        this.mongoRepo = mongoRepo;
        this.mongoSeeder = mongoSeeder;
    }

    /**
     * GET /api/inventory/seed-mongo
     * One-click trigger to seed MongoDB product details if empty.
     */
    @GetMapping("/seed-mongo")
    public Map<String, Object> seedMongo() {
        int count = mongoSeeder.seedIfEmpty();
        return Map.of("status", "SUCCESS", "seededCount", count, "message", "MongoDB seeded with product images and specs.");
    }

    // ─── Catalog ─────────────────────────────────────────────────────────────

    /**
     * GET /api/inventory/all?category=&search=
     * Returns grouped product cards for the storefront grid.
     */
    @GetMapping("/all")
    public List<GroupedProductDTO> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        List<Inventory> allItems;

        if (search != null && !search.isBlank()) {
            allItems = inventoryRepo.search(search.trim());
        } else if (category != null && !category.isBlank()) {
            allItems = inventoryRepo.findByCategory(category.trim());
        } else {
            allItems = inventoryRepo.findAll();
        }

        // Aggregate multi-warehouse records into one card per prodId
        Map<String, GroupedProductDTO> grouped = new LinkedHashMap<>();

        for (Inventory inv : allItems) {
            String pid = inv.getProdId();

            grouped.computeIfAbsent(pid, k -> {
                GroupedProductDTO dto = new GroupedProductDTO();
                dto.setProdId(pid);
                dto.setProdName(inv.getProdName());
                dto.setCategory(inv.getCategory());
                dto.setSellingPrice(inv.getSellingPrice());
                dto.setTotalStock(0);
                dto.setWarehouses(new HashSet<>());
                dto.setSuppliers(new HashSet<>());

                ProductDetails details = mongoRepo.findByProdId(pid)
                        .orElseGet(() -> {
                            ProductDetails empty = new ProductDetails();
                            empty.setProdId(pid);
                            empty.setDescription("No additional details available.");
                            empty.setSpecifications(Map.of());
                            return empty;
                        });
                dto.setExtraDetails(details);
                return dto;
            });

            GroupedProductDTO dto = grouped.get(pid);
            dto.setTotalStock(dto.getTotalStock() + inv.getStockNumber());
            dto.getWarehouses().add(inv.getWarehouseId());
            if (inv.getSupplierName() != null) {
                dto.getSuppliers().add(inv.getSupplierName());
            }
        }

        return new ArrayList<>(grouped.values());
    }

    /**
     * GET /api/inventory/{warehouseId}/{prodId}
     * Returns full detail view for a single warehouse-product pair.
     */
    @GetMapping("/{warehouseId}/{prodId}")
    public FullProductDTO getFullProduct(@PathVariable Long warehouseId,
                                         @PathVariable String prodId) {
        Inventory inv = inventoryRepo.findByWarehouseAndProdId(warehouseId, prodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found: " + prodId + " at warehouse " + warehouseId));

        ProductDetails details = mongoRepo.findByProdId(prodId).orElseGet(() -> {
            ProductDetails empty = new ProductDetails();
            empty.setDescription("No extra details available.");
            empty.setSpecifications(Map.of());
            return empty;
        });

        return new FullProductDTO(inv, details);
    }

    /**
     * GET /api/inventory/categories
     * Returns a distinct list of all product categories.
     */
    @GetMapping("/categories")
    public List<String> getCategories() {
        return inventoryRepo.findAll().stream()
                .map(Inventory::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    // ─── Cart Operations ─────────────────────────────────────────────────────

    /**
     * POST /api/inventory/cart/add/{prodId}
     * Decrements stock by 1 from any warehouse that has availability.
     * Returns 409 if out of stock.
     */
    @PostMapping("/cart/add/{prodId}")
    public ResponseEntity<String> addToCart(@PathVariable String prodId) {
        List<Inventory> items = inventoryRepo.findByProdId(prodId);

        for (Inventory item : items) {
            if (item.getStockNumber() > 0) {
                int affected = inventoryRepo.decrementStock(item.getWarehouseId(), prodId);
                if (affected > 0) {
                    return ResponseEntity.ok("Added to cart.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Product out of stock.");
    }

    /**
     * POST /api/inventory/cart/remove/{prodId}
     * Returns 1 unit back to stock (e.g., user removed item from cart).
     */
    @PostMapping("/cart/remove/{prodId}")
    public ResponseEntity<String> removeFromCart(@PathVariable String prodId) {
        List<Inventory> items = inventoryRepo.findByProdId(prodId);
        if (items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        inventoryRepo.incrementStock(items.get(0).getWarehouseId(), prodId);
        return ResponseEntity.ok("Removed from cart.");
    }

    /**
     * POST /api/inventory/checkout
     * Bulk purchase: for each item in the cart, decrement the requested qty.
     * Returns 409 if any item is insufficient in stock.
     */
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestBody CheckoutRequest request) {
        for (Map.Entry<String, Integer> entry : request.items().entrySet()) {
            String prodId  = entry.getKey();
            int    qty     = entry.getValue();

            List<Inventory> items = inventoryRepo.findByProdId(prodId);
            int totalAvail = items.stream().mapToInt(Inventory::getStockNumber).sum();

            if (totalAvail < qty) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Insufficient stock for product: " + prodId);
            }

            int remaining = qty;
            for (Inventory item : items) {
                if (remaining <= 0) break;
                int toDeduct = Math.min(remaining, item.getStockNumber());
                for (int i = 0; i < toDeduct; i++) {
                    inventoryRepo.decrementStock(item.getWarehouseId(), prodId);
                }
                remaining -= toDeduct;
            }
        }
        return ResponseEntity.ok("Order placed successfully.");
    }

    // ─── Admin CRUD ───────────────────────────────────────────────────────────

    /**
     * POST /api/inventory/add  [ADMIN only]
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FullProductDTO> addProduct(@RequestBody FullProductDTO newProduct) {
        Inventory savedInv = inventoryRepo.save(newProduct.inventoryData());

        ProductDetails extra = newProduct.extraDetails();
        if (extra != null) {
            extra.setProdId(savedInv.getProdId());
            mongoRepo.save(extra);
        }

        ProductDetails details = mongoRepo.findByProdId(savedInv.getProdId()).orElse(extra);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FullProductDTO(savedInv, details));
    }

    /**
     * PUT /api/inventory/update  [ADMIN only]
     */
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateProduct(@RequestBody FullProductDTO updateRequest) {
        Inventory incoming = updateRequest.inventoryData();

        inventoryRepo.findByWarehouseAndProdId(incoming.getWarehouseId(), incoming.getProdId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));

        inventoryRepo.update(incoming);

        if (updateRequest.extraDetails() != null) {
            mongoRepo.findByProdId(incoming.getProdId()).ifPresent(existing -> {
                ProductDetails patch = updateRequest.extraDetails();
                if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
                    existing.setDescription(patch.getDescription());
                }
                if (patch.getBrand() != null) existing.setBrand(patch.getBrand());
                if (patch.getImageUrl() != null) existing.setImageUrl(patch.getImageUrl());
                if (patch.getSpecifications() != null) existing.setSpecifications(patch.getSpecifications());
                mongoRepo.save(existing);
            });
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/inventory/delete/{warehouseId}/{prodId}  [ADMIN only]
     */
    @DeleteMapping("/delete/{warehouseId}/{prodId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long warehouseId,
                                               @PathVariable String prodId) {
        inventoryRepo.findByWarehouseAndProdId(warehouseId, prodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));

        inventoryRepo.deleteByWarehouseAndProdId(warehouseId, prodId);

        // Only delete MongoDB doc if no other warehouses still stock this product
        boolean otherWarehousesExist = !inventoryRepo.findByProdId(prodId).isEmpty();
        if (!otherWarehousesExist) {
            mongoRepo.findByProdId(prodId).ifPresent(mongoRepo::delete);
        }

        return ResponseEntity.noContent().build();
    }

    // ─── Supporting Lists ─────────────────────────────────────────────────────

    /** GET /api/suppliers */
    @GetMapping("/suppliers")
    public List<com.example.warehouse.model.Supplier> getSuppliers() {
        return inventoryRepo.findAllSuppliers();
    }

    /** GET /api/warehouses */
    @GetMapping("/warehouses")
    public List<com.example.warehouse.model.Warehouse> getWarehouses() {
        return inventoryRepo.findAllWarehouses();
    }
}
