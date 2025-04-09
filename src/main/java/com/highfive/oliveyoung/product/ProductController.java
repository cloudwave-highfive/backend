package com.highfive.oliveyoung.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product,
            @RequestParam Integer quantity
    ) {
        Product created = productService.create(product, quantity);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ProductWithStockRes>> getAllProducts() {
        List<ProductWithStockRes> all = productService.getAllProducts();
        return ResponseEntity.ok(all);
    }

    @DeleteMapping("/{productName}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productName) {
        productService.delete(productName);
        return ResponseEntity.noContent().build();
    }
}
