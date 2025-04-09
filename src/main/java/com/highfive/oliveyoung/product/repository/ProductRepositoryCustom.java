package com.highfive.oliveyoung.product.repository;

import com.highfive.oliveyoung.product.ProductWithStockRes;

import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductWithStockRes> findAllWithStock();
}
