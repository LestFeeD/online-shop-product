package com.shopir.product.dto.requestDto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class NewProductRequest {

    @Size(min=4, max=100, message = "Размер названия должен быт от 4 до 100.")
    @NotEmpty(message = "Название не должно быть пустым.")
    private String name;
    @NotEmpty(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull(message = "Цена не должна быть пустой.")
    private BigDecimal price;
    @NotNull(message = "Значение не должно быть пустым.")
    private Long idProductCategory;
    @NotNull(message = "Значение не должно быть пустым.")
    @JsonDeserialize(contentAs = Long.class)
    private List<Long> idCharacteristics;
    @JsonDeserialize(contentAs = Long.class)
    private List<Long> idWarehouse;
    @NotNull(message = "Значение не должно быть пустым.")
    private List<Integer> valueProduct;
    @JsonDeserialize(contentAs = Long.class)
    private List<Long> quantitySet;
}
