package com.shopir.product.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InventoryResponseDto {
    private String nameProduct;
    private Integer warehouseNumber;
    private Long quantity;
    public InventoryResponseDto() {
    }

    public InventoryResponseDto(String nameProduct, Integer warehouseNumber, Long quantity) {
        this.nameProduct = nameProduct;
        this.warehouseNumber = warehouseNumber;
        this.quantity = quantity;
    }




}
