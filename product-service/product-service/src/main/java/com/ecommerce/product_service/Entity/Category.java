package com.ecommerce.product_service.Entity;

import com.ecommerce.product_service.Entity.Product;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    @Column(name = "category_name")
    private String name;

    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Product> products;

}
