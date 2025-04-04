package com.highfive.oliveyoung.product;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    void deleteByProductName(String productName);
}
