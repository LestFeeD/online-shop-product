package com.shopir.product.dto.requestDto;

import com.shopir.product.entity.Product;
import com.shopir.product.entity.Supplier;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
public class NewSupplyGoodsRequestDto {
    @NotNull(message = "Значение не должно быть пустым.")
    private Long idSupplier;
    @NotNull(message = "Значение не должно быть пустым.")
    private Long idProduct;
    @NotNull(message = "Значение не должно быть пустым.")
    private BigDecimal totalCost;
}
