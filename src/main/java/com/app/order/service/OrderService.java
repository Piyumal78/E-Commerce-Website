package com.app.order.service;

import com.app.order.model.OrderItem;
import com.app.order.model.OrderModel;
import com.app.order.repo.OrderItemRepo;
import com.app.order.repo.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;


        public OrderModel createOrder(OrderModel order) {
            String orderNumber = "ord-"+System.currentTimeMillis();
            order.setOrderNumber(orderNumber);
            if (order.getItems() != null) {
                order.getItems().forEach(item -> item.setOrder(order));  // <-- THIS is crucial
                float totalAmount = 0;
                for (OrderItem item : order.getItems()) {
                    totalAmount += item.getPrice() * item.getQuantity();
                }order.setAmount(totalAmount);
            }else {
                throw new RuntimeException("Order items cannot be empty");
            }

            return orderRepo.save(order);
        }
        public List<OrderModel> getAllOrders() {
        return orderRepo.findAll();
        }
        public OrderModel getOrderById(Long orderId) {
            Optional<OrderModel> order = orderRepo.findById(orderId);
            if(order.isPresent()) {
                return order.get();
            }else {
                throw new RuntimeException("Order not found"+" "+orderId);
            }
        }
    public OrderModel updateOrder(Long id, OrderModel updatedOrder) {
        return orderRepo.findById(id).map(order -> {
            order.setOrderNumber(updatedOrder.getOrderNumber());
            order.setOrderDate(updatedOrder.getOrderDate());

            order.getItems().clear();
            if (updatedOrder.getItems() != null) {
                for (OrderItem item : updatedOrder.getItems()) {
                    item.setOrder(order);
                    order.getItems().add(item);
                }

                // Recalculate total amount
                float totalAmount = 0;
                for (OrderItem item : order.getItems()) {
                    totalAmount += item.getQuantity() * item.getPrice();
                }
                order.setAmount(totalAmount);
            } else {
                order.setAmount(0);
            }

            return orderRepo.save(order);
        }).orElseThrow(() -> new RuntimeException("Order not found " + id));
    }

    public void deleteOrder(Long orderId) {
            orderRepo.findById(orderId).orElseThrow(()->new RuntimeException("Order not found" + orderId));
            orderRepo.deleteById(orderId);
        }
}
