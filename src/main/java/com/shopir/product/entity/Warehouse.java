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
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idWarehouse;
    private Integer warehouseNumber;
    private String street;
    private String home;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<Inventory> inventories;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<AddressWarehouse> addressWarehouses;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(idWarehouse, warehouse.idWarehouse) && Objects.equals(warehouseNumber, warehouse.warehouseNumber) && Objects.equals(street, warehouse.street) && Objects.equals(home, warehouse.home) && Objects.equals(inventories, warehouse.inventories) && Objects.equals(addressWarehouses, warehouse.addressWarehouses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idWarehouse, warehouseNumber, street, home, inventories, addressWarehouses);
    }
}
