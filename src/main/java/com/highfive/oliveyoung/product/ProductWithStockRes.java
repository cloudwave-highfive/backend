package com.highfive.oliveyoung.product;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
public class ProductWithStockRes {
    @Indexed(unique = true)
    private final String productName;
    private final int price;
    private final int quantity;

    @Builder
    private ProductWithStockRes(String productName, int price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
