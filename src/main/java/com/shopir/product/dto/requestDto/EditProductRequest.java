package com.shopir.product.dto.requestDto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long idProductCategory;
    private Long oldIdCharacteristic;
    private Long idCharacteristic;
    private Long idWarehouse;
    private Integer valueProduct;
    private Long quantity;
}
