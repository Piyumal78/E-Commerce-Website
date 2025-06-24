package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
