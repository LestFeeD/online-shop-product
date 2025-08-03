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
@Table(name = "product_characteristics")
public class ProductCharacteristics {

    @EmbeddedId
    private  IdProductCharacteristics idProductCharacteristics;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProduct")
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCharacteristic")
    @JoinColumn(name = "id_characteristic")
    private Characteristic characteristic;

    private Integer valueProduct;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductCharacteristics that = (ProductCharacteristics) o;
        return Objects.equals(idProductCharacteristics, that.idProductCharacteristics) && Objects.equals(product, that.product) && Objects.equals(characteristic, that.characteristic) && Objects.equals(valueProduct, that.valueProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProductCharacteristics, product, characteristic, valueProduct);
    }
}
