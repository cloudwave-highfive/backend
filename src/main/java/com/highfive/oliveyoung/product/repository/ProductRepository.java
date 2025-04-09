package com.highfive.oliveyoung.product.repository;

import com.highfive.oliveyoung.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String>, ProductRepositoryCustom {
    void deleteByProductName(String productName);
}
