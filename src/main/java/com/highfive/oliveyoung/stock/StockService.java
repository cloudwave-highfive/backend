package com.highfive.oliveyoung.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public Stock updateStock(String productName, int quantity) {
        Stock stock = stockRepository.findByProductName(productName)
                .orElse(Stock.builder()
                        .productName(productName)
                        .quantity(0)
                        .build());

        stock.setQuantity(quantity);
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock decrementStock(String productName) {
        Stock stock = stockRepository.findByProductName(productName)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        stock.decrementQuantity();
        return stockRepository.save(stock);
    }
}
