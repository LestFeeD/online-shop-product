package com.shopir.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supply_goods")
public class SupplyGoods {

    @EmbeddedId
    private  IdSupplyGoods idSupplyGoods;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idSupplier")
    @JoinColumn(name = "id_supplier")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idProduct")
    @JoinColumn(name = "id_product")
    private Product product;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    private Date dateSupply;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SupplyGoods that = (SupplyGoods) o;
        return Objects.equals(idSupplyGoods, that.idSupplyGoods) && Objects.equals(supplier, that.supplier) && Objects.equals(product, that.product) && Objects.equals(totalCost, that.totalCost) && Objects.equals(dateSupply, that.dateSupply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSupplyGoods, supplier, product, totalCost, dateSupply);
    }
}
