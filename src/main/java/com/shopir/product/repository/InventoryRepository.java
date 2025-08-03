package com.shopir.product.repository;

import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query(value = "SELECT new com.shopir.product.dto.responseDto.InventoryResponseDto(p.nameProduct, w.warehouseNumber, SUM(i.quantity)) " +
            "FROM Inventory i " +
            "JOIN i.warehouse w " +
            "JOIN i.product p " +
            "GROUP BY p.nameProduct, w.idWarehouse, w.warehouseNumber")
    List<InventoryResponseDto> findTotalInventory();

    @Query(value = "SELECT com.shopir.product.dto.responseDto.InventoryResponseDto(p.nameProduct, w.warehouseNumber, SUM(i.quantity)) " +
            "FROM Inventory i " +
            "JOIN i.warehouse w " +
            "JOIN i.product p " +
            "WHERE w.warehouseNumber  = :number " +
            "GROUP BY p.nameProduct, w.idWarehouse, w.warehouseNumber")
    List<InventoryResponseDto> findTotalInventoryByNumberWarehouse(@Param("number") Long number);

    List<Inventory> findByProduct_IdProduct(Long idProduct);

    Optional<Inventory> findByProduct_IdProductAndWarehouse_IdWarehouse(Long idProduct, Long idWarehouse);


}
