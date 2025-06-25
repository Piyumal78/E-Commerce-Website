package com.app.order.repo;

import com.app.order.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<OrderModel, Long> {

}
