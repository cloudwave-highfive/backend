package com.highfive.oliveyoung.product.repository;

import com.highfive.oliveyoung.product.ProductWithStockRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ProductWithStockRes> findAllWithStock() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("stock_tb", "productName", "productName", "stock"),
                Aggregation.unwind("stock", true),  // stock이 없을 경우에도 포함하고 싶다면 `true` 사용
                Aggregation.project("productName", "price")
                        .and("stock.quantity").as("quantity")
        );

        return mongoTemplate.aggregate(aggregation, "product_tb", ProductWithStockRes.class).getMappedResults();
    }
}
