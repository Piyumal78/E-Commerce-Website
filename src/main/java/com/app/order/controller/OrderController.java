package com.app.order.controller;

import com.app.order.model.OrderModel;
import com.app.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderModel> createOrder(@RequestBody OrderModel order) {
        return new ResponseEntity<>(orderService.createOrder(order), HttpStatus.CREATED);
    }

    @GetMapping
    public Object getAllOrders() {
        return orderService.getAllOrders();
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderModel> getOrderById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<OrderModel> updateOrder(@PathVariable("id") Long id, @RequestBody OrderModel updatedOrder) {
        return new ResponseEntity<>(orderService.updateOrder(id, updatedOrder), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
    }
}
