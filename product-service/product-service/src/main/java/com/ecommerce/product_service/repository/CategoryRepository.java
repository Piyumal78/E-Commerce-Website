package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
