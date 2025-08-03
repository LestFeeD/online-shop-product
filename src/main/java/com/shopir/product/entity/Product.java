package com.shopir.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;
    private String nameProduct;
    private String description;
    private BigDecimal price;
    private Byte  isDeleted;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;


    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Inventory> inventories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ProductCharacteristics> productCharacteristics;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(idProduct, product.idProduct) && Objects.equals(nameProduct, product.nameProduct) && Objects.equals(description, product.description) && Objects.equals(price, product.price) && Objects.equals(isDeleted, product.isDeleted) && Objects.equals(productCategory, product.productCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, nameProduct, description, price, isDeleted, productCategory);
    }
}
