package com.example.warehouse.repository;

import com.example.warehouse.model.ProductDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the {@code product_details} collection.
 */
public interface ProductDetailsRepository extends MongoRepository<ProductDetails, String> {

    /** Primary cross-DB lookup: find by MySQL prodId (safe against non-unique results) */
    Optional<ProductDetails> findFirstByProdId(String prodId);

    default Optional<ProductDetails> findByProdId(String prodId) {
        return findFirstByProdId(prodId);
    }

    /** Tag-based search */
    @Query("{ 'tags': { $in: ?0 } }")
    List<ProductDetails> findByTagsIn(List<String> tags);
}
