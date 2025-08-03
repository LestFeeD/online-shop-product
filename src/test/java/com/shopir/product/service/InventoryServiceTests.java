package com.shopir.product.service;

import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.entity.Inventory;
import com.shopir.product.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTests {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private  InventoryRepository inventoryRepository;

    @Test
    void findAllInventory_findTotalInventoryEveryone_returnListInventoryResponseDto() {
        List<InventoryResponseDto> inventoryResponseDto = Mockito.mock();

        when(inventoryRepository.findTotalInventory()).thenReturn(inventoryResponseDto);

        inventoryService.findAllInventory();
        verify(inventoryRepository).findTotalInventory();
    }
    @Test
    void findAllInventory_notFoundInventory_returnEmptyList() {

        when(inventoryRepository.findTotalInventory()).thenReturn(new ArrayList<>());

        inventoryService.findAllInventory();
        verify(inventoryRepository).findTotalInventory();
    }

    @Test
    void findInventory_findInventory_returnListInventoryResponseDto() {
        List<InventoryResponseDto> inventoryResponseDto = Mockito.mock();
        Long number = 1L;
        when(inventoryRepository.findTotalInventoryByNumberWarehouse(number)).thenReturn(inventoryResponseDto);

        inventoryService.findInventory(number);
        verify(inventoryRepository).findTotalInventoryByNumberWarehouse(number);
    }

    @Test
    void deleteProductFromWarehouse_deleteProductFromWarehouseWithParameters() {
        Inventory inventory = Mockito.mock();
        when(inventoryRepository.findByProduct_IdProductAndWarehouse_IdWarehouse(1L, 1L)).thenReturn(Optional.ofNullable(inventory));
        doNothing().when(inventoryRepository).delete(inventory);

        inventoryService.deleteProductFromWarehouse(1L, 1L);
        verify(inventoryRepository).findByProduct_IdProductAndWarehouse_IdWarehouse(1L, 1L);
        verify(inventoryRepository).delete(inventory);
    }
}
