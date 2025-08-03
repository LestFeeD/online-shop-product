package com.shopir.product.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
public class ProductResponseDto implements Serializable {

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long idProduct, String nameProduct, String description, BigDecimal  price, String nameCategory, Set<String> nameCharacteristics, Set<Integer> valueProduct) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.description = description;
        this.price = price;
        this.nameCategory = nameCategory;
        this.nameCharacteristics = nameCharacteristics;
        this.valueProduct = valueProduct;
    }

    private Long idProduct;
    private String nameProduct;
    private String description;
    private BigDecimal price;
    private String nameCategory;
    private Set<String> nameCharacteristics;
    private Set<Integer> valueProduct;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductResponseDto that = (ProductResponseDto) o;
        return Objects.equals(idProduct, that.idProduct) && Objects.equals(nameProduct, that.nameProduct) && Objects.equals(description, that.description) && Objects.equals(price, that.price) && Objects.equals(nameCategory, that.nameCategory) && Objects.equals(nameCharacteristics, that.nameCharacteristics) && Objects.equals(valueProduct, that.valueProduct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, nameProduct, description, price, nameCategory, nameCharacteristics, valueProduct);
    }

    @Override
    public String toString() {
        return "ProductResponseDto{" +
                "id=" + idProduct +
                ", nameProduct='" + nameProduct + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", nameCategory='" + nameCategory + '\'' +
                ", characteristics=" + nameCharacteristics +
                ", valueProduct=" + valueProduct +
                '}';
    }
}
