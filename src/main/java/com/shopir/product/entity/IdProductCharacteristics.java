package com.shopir.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class IdProductCharacteristics implements Serializable {

    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "id_characteristic")
    private Long idCharacteristic;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IdProductCharacteristics that = (IdProductCharacteristics) o;
        return Objects.equals(idProduct, that.idProduct) && Objects.equals(idCharacteristic, that.idCharacteristic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, idCharacteristic);
    }

    @Override
    public String toString() {
        return "IdProductCharacteristics{" +
                "idProduct=" + idProduct +
                ", idCharacteristic=" + idCharacteristic +
                '}';
    }
}
