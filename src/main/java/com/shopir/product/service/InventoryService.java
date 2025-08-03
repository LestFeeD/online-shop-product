package com.shopir.product.service;

import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.entity.Inventory;
import com.shopir.product.entity.Product;
import com.shopir.product.exceptions.NotFoundException;
import com.shopir.product.factories.InventoryFactory;
import com.shopir.product.factories.ProductFactory;
import com.shopir.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Cacheable(value = "inventory")
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> findAllInventory() throws NotFoundException {

        List<InventoryResponseDto> totalInventoryByWarehouse = inventoryRepository.findTotalInventory();
        if (totalInventoryByWarehouse == null || totalInventoryByWarehouse.isEmpty()) {
            return new ArrayList<>();
        } else {
            return totalInventoryByWarehouse;
        }
    }

    @Cacheable(value = "inventory", key = "#number")
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> findInventory(Long number) throws NotFoundException {

        List<InventoryResponseDto> inventoryResponseDto = inventoryRepository.findTotalInventoryByNumberWarehouse(number);
        if (inventoryResponseDto == null ) {
            return new ArrayList<>();
        } else {
            return inventoryResponseDto;
        }
    }

    @CacheEvict(value = "inventory")
    @Transactional
    public void deleteProductFromWarehouse(Long idProduct, Long idWarehouse) {
        Inventory inventory = inventoryRepository.findByProduct_IdProductAndWarehouse_IdWarehouse(idProduct, idWarehouse)
                .orElseThrow(() -> {
                    logger.warn("Inventory with idProduct={} and idWarehouse={} not found" , idProduct, idWarehouse);
                    return new NotFoundException("Inventory not found");
                });
        inventoryRepository.delete(inventory);
    }

}
