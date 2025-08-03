package com.shopir.product.controller;

import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @GetMapping("/all-products")
    public ResponseEntity<List<InventoryResponseDto>> findAllProduct()  {
        List<InventoryResponseDto> inventoryList =  inventoryService.findAllInventory();
        return ResponseEntity.ok(inventoryList);

    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @GetMapping("/all-products/warehouse/{numberWarehouse}")
    public ResponseEntity<List<InventoryResponseDto>> findAllProductInWarehouse(@PathVariable(value = "numberWarehouse") Long numberWarehouse )  {
        List<InventoryResponseDto> inventoryList =  inventoryService.findInventory(numberWarehouse);
        return ResponseEntity.ok(inventoryList);

    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @DeleteMapping("/products/{idProduct}/warehouse/{idWarehouse}")
    public ResponseEntity<Void> deleteProductFromWarehouse(@PathVariable(value = "idProduct") Long idProduct, @PathVariable(value = "idWarehouse") Long idWarehouse) {
        inventoryService.deleteProductFromWarehouse(idProduct, idWarehouse);
        return ResponseEntity.noContent().build();

    }
}
