package com.highfive.oliveyoung.stock;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document(collection = "stock_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access =AccessLevel.PRIVATE)
public class Stock {
    @Id
    private String id;
    private String productName;
    @Setter
    private int quantity;

    public void decrementQuantity() {
        this.quantity--;
    }
}
