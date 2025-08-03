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
@Table(name = "address_warehouse")
public class AddressWarehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAddressWarehouse;
    private String street;
    private String home;

    @ManyToOne
    @JoinColumn(name = "id_warehouse")
    private Warehouse warehouse;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AddressWarehouse that = (AddressWarehouse) o;
        return Objects.equals(idAddressWarehouse, that.idAddressWarehouse) && Objects.equals(street, that.street) && Objects.equals(home, that.home) && Objects.equals(warehouse, that.warehouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAddressWarehouse, street, home, warehouse);
    }

    @Override
    public String toString() {
        return "AddressWarehouse{" +
                "idAddressWarehouse=" + idAddressWarehouse +
                ", street='" + street + '\'' +
                ", home='" + home + '\'' +
                ", warehouse=" + warehouse +
                '}';
    }
}
