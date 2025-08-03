package com.shopir.product.factories;

import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.entity.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductFactory {
    public ProductResponseDto makeProductDto(Product entity) {

        return ProductResponseDto.builder()
                .idProduct(entity.getIdProduct())
                .nameProduct(entity.getNameProduct())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .nameCategory(entity.getProductCategory().getName())
                .nameCharacteristics(entity.getProductCharacteristics().stream()
                        .map(pc -> pc.getCharacteristic().getName())
                        .collect(Collectors.toSet()))
                .valueProduct(entity.getProductCharacteristics().stream()
                        .map(ProductCharacteristics::getValueProduct)
                        .collect(Collectors.toSet()))
                .build();
    }
}
