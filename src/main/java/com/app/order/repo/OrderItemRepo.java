package com.app.order.repo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDto extends JpaRepository<OrderItemDto, Long> {
    
}
