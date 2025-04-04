package com.highfive.oliveyoung.stock;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByProductName(String productName);
    void deleteByProductName(String productName);
}
