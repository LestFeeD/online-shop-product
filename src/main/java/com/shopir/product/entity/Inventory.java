package com.shopir.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventory;
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_warehouse")
    private Warehouse warehouse;

    private Long quantity;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(idInventory, inventory.idInventory) && Objects.equals(product, inventory.product) && Objects.equals(warehouse, inventory.warehouse) && Objects.equals(quantity, inventory.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idInventory, product, warehouse, quantity);
    }
}
