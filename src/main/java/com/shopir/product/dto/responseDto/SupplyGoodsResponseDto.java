package com.shopir.product.dto.responseDto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
public class SupplyGoodsResponseDto {

    private String nameSupplier;

    private String nameProduct;

    private BigDecimal totalCost;

    private Date dateSupply;
}
