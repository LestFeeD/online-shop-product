package com.shopir.product.service;

import com.shopir.product.dto.requestDto.EditProductRequest;
import com.shopir.product.dto.requestDto.NewProductRequest;
import com.shopir.product.entity.*;
import com.shopir.product.factories.ProductFactory;
import com.shopir.product.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private  ProductCategoryRepository productCategoryRepository;

    @Mock
    private CharacteristicRepository characteristicsRepository;

    @Mock
    private  ProductCharacteristicsRepository productCharacteristicsRepository;

    @Mock
    private  WarehouseRepository warehouseRepository;

    @Mock
    private  ProductFactory productFactory;
    @Mock
    private BindingResult bindingResult;
    @Test
    void findProductById_findProduct_returnProductResponseDto() {

        Product product = Mockito.mock();
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));

        productService.findProductById(1L);
        verify(productRepository).findById(1L);
        verify(productFactory).makeProductDto(product);
    }

    @Test
    void findAllProduct_findAllProduct_returnProductResponseDto() {

        List<Product> products = Mockito.mock();
        when(productRepository.findAllByIsDeleted((byte) 0)).thenReturn(products);

        productService.findAllProduct();
        verify(productRepository).findAllByIsDeleted((byte) 0);
    }

    @Test
    void findProduct_findProductByName_returnProductResponseDto() {
        Product product = Mockito.mock();
        String name = "test";
        when(productRepository.findByNameProductIgnoreCaseAndIsDeleted(name, (byte) 0)).thenReturn(product);

        productService.findProduct(name, null);
        verify(productRepository).findByNameProductIgnoreCaseAndIsDeleted(name, (byte) 0);
        verify(productFactory).makeProductDto(product);

    }

    @Test
    void suggestProductsByName_findByPartOfName_returnListProductResponseDto() {
        List<Product> products = Mockito.mock();
        String name = "test";
        when(productRepository.findByNameProductContainingIgnoreCaseAndIsDeletedIsNull(name)).thenReturn(products);

        productService.suggestProductsByName(name);
        verify(productRepository).findByNameProductContainingIgnoreCaseAndIsDeletedIsNull(name);

    }

    @Test
    void addNewProduct_addNewProductInSystem() {
        List<Long> warehouseIds = List.of(1L, 2L);
        List<Long> quantitySet = List.of(10L, 20L);
        List<Long> characteristicIds = List.of(100L, 200L);
        List<Integer> values = List.of(1, 1);

        NewProductRequest request = NewProductRequest.builder()
                .idWarehouse(warehouseIds)
                .quantitySet(quantitySet)
                .idCharacteristics(characteristicIds)
                .valueProduct(values)
                .idProductCategory(1L)
                .build();
        ProductCategory productCategory = Mockito.mock();
        Product product = Mockito.mock();
        Characteristic characteristic = Mockito.mock();
        ProductCharacteristics productCharacteristics = Mockito.mock();
        Warehouse warehouse = Mockito.mock();
        Inventory inventory = Mockito.mock();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(productCategoryRepository.findById(request.getIdProductCategory())).thenReturn(Optional.ofNullable(productCategory));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        for (Long id : request.getIdCharacteristics()) {

            when(characteristicsRepository.findById(id)).thenReturn(Optional.of(Mockito.mock(Characteristic.class)));
        }
        when(productCharacteristicsRepository.save(any(ProductCharacteristics.class))).thenReturn(productCharacteristics);
        for (Long id : request.getIdWarehouse()) {
            when(warehouseRepository.findById(id)).thenReturn(Optional.ofNullable(warehouse));
        }
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        productService.addNewProduct(request, bindingResult);

        verify(productRepository).save(any(Product.class));
        verify(productCharacteristicsRepository, times(2)).save(any(ProductCharacteristics.class));
        verify(inventoryRepository, times(2)).save(any(Inventory.class));

    }

    @Test
    void editProduct_editProduct() {
        Product product = Mockito.mock();
        ProductCategory productCategory = Mockito.mock();
        Warehouse warehouse = Mockito.mock();
        Inventory inventory = Mockito.mock();
        ProductCharacteristics productCharacteristics = Mockito.mock();
        Characteristic characteristic = Mockito.mock();

        EditProductRequest request = EditProductRequest.builder()
                .idProductCategory(1L)
                .oldIdCharacteristic(2L)
                .idCharacteristic(1L)
                .idWarehouse(1L)
                .valueProduct(22)
                .quantity(1L)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(productCategoryRepository.findById(request.getIdProductCategory())).thenReturn(Optional.ofNullable(productCategory));
        when(warehouse.getIdWarehouse()).thenReturn(1L);
        when(inventory.getWarehouse()).thenReturn(warehouse);
        when(characteristicsRepository.findById(1L)).thenReturn(Optional.ofNullable(characteristic));
        when(inventoryRepository.findByProduct_IdProductAndWarehouse_IdWarehouse(1L, request.getIdWarehouse())).thenReturn(Optional.ofNullable(inventory));
        when(warehouseRepository.findById(request.getIdWarehouse())).thenReturn(Optional.ofNullable(warehouse));

        when(productCharacteristicsRepository
                .findByProduct_IdProductAndCharacteristic_IdCharacteristic(1L, request.getOldIdCharacteristic()))
                .thenReturn(productCharacteristics);

        when(inventoryRepository.saveAndFlush(inventory)).thenReturn(inventory);
        when(productRepository.saveAndFlush(product)).thenReturn(product);

        productService.editProduct(1L, request);
    }

    @Test
    void deleteProduct_deleteProduct() {
        Product product = Mockito.mock();
        Inventory inventory1 = Mockito.mock();
        Inventory inventory2 = Mockito.mock();
        List<Inventory> inventories = List.of(inventory1, inventory2);

        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(inventoryRepository.findByProduct_IdProduct(1L)).thenReturn(inventories);
        for (Inventory inventory : inventories) {
            doNothing().when(inventoryRepository).delete(inventory);
        }
        productService.deleteProduct(1L);

    }
}
