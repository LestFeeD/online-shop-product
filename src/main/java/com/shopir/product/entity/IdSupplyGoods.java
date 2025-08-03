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
public class IdSupplyGoods implements Serializable {

    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "id_supplier")
    private Long idSupplier;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IdSupplyGoods that = (IdSupplyGoods) o;
        return Objects.equals(idProduct, that.idProduct) && Objects.equals(idSupplier, that.idSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, idSupplier);
    }
}
