package com.highfive.oliveyoung.product;

import com.highfive.oliveyoung.product.repository.ProductRepository;
import com.highfive.oliveyoung.stock.Stock;
import com.highfive.oliveyoung.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional
    public Product create(Product product, Integer quantity) {
        Product saved = productRepository.save(product);

        // 상품 등록 시 재고도 초기화 (수량: quantity)
        stockRepository.save(
                Stock.builder()
                        .productName(saved.getProductName())
                        .quantity(quantity)
                        .build()
        );

        return saved;
    }

    public List<ProductWithStockRes> getAllProducts() {
        return productRepository.findAllWithStock();
    }

    @Transactional
    public void delete(String productName) {
        productRepository.deleteByProductName(productName);
        stockRepository.deleteByProductName(productName);
    }
}