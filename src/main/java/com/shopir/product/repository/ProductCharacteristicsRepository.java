package com.shopir.product.repository;

import com.shopir.product.entity.Product;
import com.shopir.product.entity.ProductCharacteristics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductCharacteristicsRepository extends JpaRepository<ProductCharacteristics, Long> {
    ProductCharacteristics findByProduct_IdProductAndCharacteristic_IdCharacteristic(Long idProduct, Long idCharacteristic);
    ProductCharacteristics findByProduct_IdProduct(Long idProduct);
    @Query("SELECT p FROM Product p JOIN FETCH p.productCharacteristics WHERE p.id = :id")
    Product findProductByIdWithCharacteristic(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM ProductCharacteristics pc WHERE pc.idProductCharacteristics.idProduct = :idProduct AND pc.idProductCharacteristics.idCharacteristic = :idCharacteristic")
    void deleteByProductIdAndCharacteristicId(@Param("idProduct") Long idProduct, @Param("idCharacteristic") Long idCharacteristic);
}
