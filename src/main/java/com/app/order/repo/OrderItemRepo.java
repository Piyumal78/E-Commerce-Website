package com.app.order.repo;

import com.app.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
