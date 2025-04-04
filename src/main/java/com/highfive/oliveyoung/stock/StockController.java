package com.highfive.oliveyoung.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PutMapping("/{productName}")
    public ResponseEntity<Stock> updateStock(
            @PathVariable String productName,
            @RequestParam int quantity) {
        return ResponseEntity.ok(stockService.updateStock(productName, quantity));
    }

    @PutMapping("/{productName}/purchase")
    public ResponseEntity<Stock> purchaseProduct(@PathVariable String productName) {
        return ResponseEntity.ok(stockService.decrementStock(productName));
    }
}