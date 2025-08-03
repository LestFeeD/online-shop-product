package com.shopir.product.dto.responseDto;

import java.math.BigDecimal;

public interface ProductPopularProjection {
    Long getIdProduct();
    String getNameProduct();
    String getDescription();
    BigDecimal getPrice();
    String getNameCategory();
    String getNameCharacteristic();
    Integer getValueProduct();
}

