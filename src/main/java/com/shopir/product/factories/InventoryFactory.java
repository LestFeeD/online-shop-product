package com.shopir.product.factories;

import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryFactory {
    public InventoryResponseDto makeProductQuantityDto(Inventory entity) {

        return InventoryResponseDto.builder()
                .warehouseNumber(entity.getWarehouse().getWarehouseNumber())
                .nameProduct(entity.getProduct().getNameProduct())
                .quantity(entity.getQuantity())
                .build();
    }
}
