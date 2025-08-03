package com.shopir.product.factories;

import com.shopir.product.dto.responseDto.SupplyGoodsResponseDto;
import com.shopir.product.entity.SupplyGoods;
import org.springframework.stereotype.Component;

@Component
public class SupplyGoodsFactory {
    public SupplyGoodsResponseDto makeSupplyGoodsDto(SupplyGoods entity) {

        return SupplyGoodsResponseDto.builder()
                .nameProduct(entity.getProduct().getNameProduct())
                .nameProduct(entity.getProduct().getNameProduct())
                .dateSupply(entity.getDateSupply())
                .totalCost(entity.getTotalCost())
                .build();
    }
}
