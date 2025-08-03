package com.shopir.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_category")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductCategory;
    private String name;
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Product> products;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory that = (ProductCategory) o;
        return Objects.equals(idProductCategory, that.idProductCategory) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProductCategory, name);
    }
}
