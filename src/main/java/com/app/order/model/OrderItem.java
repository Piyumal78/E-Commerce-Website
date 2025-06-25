package com.app.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String productName;
    private int quantity;
    private float price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderModel order;


}
