package com.shopir.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductKafkaDto {
    private Long idProduct;
    private String nameProduct;
    private BigDecimal price;
    private Long idCart;
    private Long idOrder;

}
