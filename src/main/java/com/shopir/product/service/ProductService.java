package com.shopir.product.service;

import com.shopir.product.dto.requestDto.EditProductRequest;
import com.shopir.product.dto.requestDto.NewProductRequest;

import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.entity.*;
import com.shopir.product.exceptions.BadRequestException;
import com.shopir.product.exceptions.NotFoundException;
import com.shopir.product.factories.ProductFactory;
import com.shopir.product.repository.*;
import com.shopir.product.utils.ValidationErrors;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CharacteristicRepository characteristicsRepository;
    private final ProductCharacteristicsRepository productCharacteristicsRepository;

    private final ValidationErrors validationErrors;

    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductFactory productFactory;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, CharacteristicRepository characteristicsRepository, ProductCharacteristicsRepository productCharacteristicsRepository, ValidationErrors validationErrors, WarehouseRepository warehouseRepository, InventoryRepository inventoryRepository, ProductFactory productFactory) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.characteristicsRepository = characteristicsRepository;
        this.productCharacteristicsRepository = productCharacteristicsRepository;
        this.validationErrors = validationErrors;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.productFactory = productFactory;
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findProductById(Long idProduct) {
        Product product = productRepository.findByIdProductAndIsDeleted(idProduct, (byte) 0).orElseThrow(() -> {
            logger.warn("Product with ID={} not found to find", idProduct);
            return new NotFoundException("Product not found");
        });

        return productFactory.makeProductDto(product);
    }

    @Cacheable(value = "product")
    @Transactional(readOnly = true)
    public  Map<String, List<ProductResponseDto>> findAllProduct() throws NotFoundException {

        List<Product> products = productRepository.findAllByIsDeleted((byte) 0);
        if (products == null || products.isEmpty()) {
            return new LinkedHashMap<>();
        } else {
            List<ProductResponseDto> productResponseDtos = products.stream()
                    .map(productFactory::makeProductDto)
                    .collect(Collectors.toList());
            return products.stream()
                    .collect(Collectors.groupingBy(
                            product -> product.getProductCategory().getName(),
                            Collectors.mapping(productFactory::makeProductDto, Collectors.toList())));
        }
    }

    @Cacheable(value = "popular_products")
    @Transactional(readOnly = true)
    public  List<ProductResponseDto> getMostPopularProducts() throws NotFoundException {

        List<Product> products = productRepository.findMostPopularProducts();
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        } else {
            return products.stream()
                    .map(productFactory::makeProductDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public ProductResponseDto findProduct(String name, Long id) throws NotFoundException {
        Product product;
        ProductResponseDto productResponseDto;
        if(name != null && name != "") {
          product = productRepository.findByNameProductIgnoreCaseAndIsDeleted(name, (byte)0);
          logger.debug("Product with ID={} to find", product.getIdProduct());
            productResponseDto = productFactory.makeProductDto(product);
        } else if (id != null ) {
          product =  productRepository.findById(id).orElseThrow();
            productResponseDto = productFactory.makeProductDto(product);
        } else {
            return new ProductResponseDto();
        }
    return productResponseDto;
    }

    //подсказка для других продуктов
    public List<ProductResponseDto> suggestProductsByName(String partialName) {
        List<Product> products = productRepository.findByNameProductContainingIgnoreCaseAndIsDeletedIsNull(partialName);
        return products.stream()
                .map(productFactory::makeProductDto)
                .collect(Collectors.toList());
    }


    @CacheEvict(value = "product")
    @Transactional
    public Long addNewProduct(  @Valid NewProductRequest request, BindingResult bindingResult) {
        if(bindingResult.hasErrors() ) {
            String result = validationErrors.getValidationErrors(bindingResult);
            logger.error("Validation errors occurred while creating event: {}", result);
            throw new BadRequestException(result);
        }

        logger.debug("Receiving data from request: name={}, description={}, price={}, idProductCategory={}, idCharacteristics={}, idWarehouse={}, " +
                "valueProduct={}, quantitySet={}", request.getName(), request.getDescription(), request.getPrice(), request.getIdProductCategory(),
                request.getIdCharacteristics(), request.getIdWarehouse(), request.getValueProduct(), request.getQuantitySet());


        ProductCategory productCategory = productCategoryRepository.findById(request.getIdProductCategory()).orElseThrow(() -> {
            logger.warn("ProductCategory with ID={} not found", request.getIdProductCategory());
            return new NotFoundException("ProductCategory not found with ID: " + request.getIdProductCategory());
        });

        Product product = Product.builder()
                .nameProduct(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .productCategory(productCategory)
                .isDeleted(Byte.valueOf((byte) 0))
                .build();
        productRepository.save(product);

        List<Long> characteristicIds = new ArrayList<>(request.getIdCharacteristics());
        List<Integer> values = new ArrayList<>(request.getValueProduct());

        for (int i = 0; i < characteristicIds.size(); i++) {
            Long characteristicId = characteristicIds.get(i);

            Characteristic ch = characteristicsRepository.findById(characteristicId)
                    .orElseThrow(() -> {
                        logger.warn("Characteristic with ID={} not found", characteristicId);
                        return new NotFoundException("Characteristic not found with ID: " + characteristicId);
                    });
            IdProductCharacteristics id = new IdProductCharacteristics();
            id.setIdProduct(product.getIdProduct());
            id.setIdCharacteristic(ch.getIdCharacteristic());
            ProductCharacteristics pc = ProductCharacteristics.builder()
                    .idProductCharacteristics(id)
                    .product(product)
                    .characteristic(ch)
                    .valueProduct(values.get(i))
                    .build();

            productCharacteristicsRepository.save(pc);

        }

        if(request.getIdWarehouse() != null && request.getQuantitySet() != null) {
            List<Long> warehouseIds = new ArrayList<>(request.getIdWarehouse());
            List<Long> quantities = new ArrayList<>(request.getQuantitySet());

            for (int i = 0; i < warehouseIds.size(); i++) {
                Long warehouseId = warehouseIds.get(i);
                Warehouse w = warehouseRepository.findById(warehouseId)
                        .orElseThrow(()  ->  {
                            logger.warn("Warehouse with ID={} not found", warehouseId);
                            return new NotFoundException("Warehouse not found");
                        });

                Inventory inventory = Inventory.builder()
                        .product(product)
                        .warehouse(w)
                        .quantity(quantities.get(i))
                        .build();

                inventoryRepository.save(inventory);
            }
        }
        return product.getIdProduct();
    }

    @CachePut(value = "product", key = "#idProduct")
    @Transactional
    public void editProduct(Long idProduct, @Valid EditProductRequest request) {
        logger.debug("Receiving data from request: idProduct={}, name={}, description={}, price={}, idProductCategory={}, idCharacteristic={}, idOldCharacteristic={}, idWarehouse={}, " +
                        "valueProduct={}, quantity={}",idProduct, request.getName(), request.getDescription(), request.getPrice(), request.getIdProductCategory(),
                request.getIdCharacteristic(), request.getOldIdCharacteristic(), request.getIdWarehouse(), request.getValueProduct(), request.getQuantity());

        Product product = productRepository.findById(idProduct).orElseThrow(() -> {
            logger.warn("Product with ID={} not found", idProduct);
            return  new NotFoundException("Product not found");

        });
        ProductCategory productCategory;
        if(request.getIdProductCategory() != null) {
            productCategory = productCategoryRepository.findById(request.getIdProductCategory()).orElseThrow();
            product.setProductCategory(productCategory);
        }




        inventoryRepository.findByProduct_IdProductAndWarehouse_IdWarehouse(idProduct, request.getIdWarehouse())
                .ifPresentOrElse(
                        inventory -> {
                            // логика при наличии inventory
                            Long warehouseId = inventory.getWarehouse() != null ? inventory.getWarehouse().getIdWarehouse() : null;

                            logger.warn("Inventory before changes: warehouse={}, quantity={} ", warehouseId, inventory.getQuantity());

                            if(request.getIdWarehouse() != null ) {
                                Warehouse warehouse = warehouseRepository.findById(request.getIdWarehouse()).orElseThrow(() -> {
                                            logger.warn("Warehouse with ID={} not found in edit method", request.getIdWarehouse());
                                            return new NotFoundException("Warehouse not found");
                                        });
                                inventory.setWarehouse(warehouse);
                            }
                                Optional.ofNullable(request.getQuantity()).ifPresent(inventory::setQuantity);

                                inventoryRepository.saveAndFlush(inventory);
                            logger.warn("Inventory after changes: warehouse={}, quantity={} ", inventory.getWarehouse().getIdWarehouse(), inventory.getQuantity());

                        },
                        () -> {
                            // логика при отсутствии inventory
                        }
                );
        ProductCharacteristics productCharacteristics;
        if(request.getIdCharacteristic() != null ) {
            productCharacteristics = productCharacteristicsRepository
                    .findByProduct_IdProductAndCharacteristic_IdCharacteristic(idProduct, request.getOldIdCharacteristic());

            if (productCharacteristics != null) {
                logger.debug("ProductCharacteristics ID={}, and Value={}", productCharacteristics.getIdProductCharacteristics(), productCharacteristics.getValueProduct());
            } else {
                logger.debug("ProductCharacteristics not found for idProduct={} and idCharacteristic={}", idProduct, request.getIdCharacteristic());
            }
        }
        else {
            productCharacteristics = null;
        }

        Optional.ofNullable(request.getName()).ifPresent(product::setNameProduct);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);


        if(request.getValueProduct() != null && request.getIdCharacteristic() == null) {
            productCharacteristics.setValueProduct(request.getValueProduct());
            productCharacteristicsRepository.saveAndFlush(productCharacteristics);

        }
        if(request.getIdCharacteristic() != null) {

            Characteristic characteristics = characteristicsRepository.findById(request.getIdCharacteristic()).orElseThrow(() -> {
                logger.warn("Characteristic with ID={} not found in edit method", productCharacteristics.getCharacteristic());
                return new NotFoundException("Characteristic not found");
            });

            productCharacteristicsRepository.deleteByProductIdAndCharacteristicId(idProduct, request.getOldIdCharacteristic());

            IdProductCharacteristics newId = new IdProductCharacteristics();
            newId.setIdProduct(product.getIdProduct());
            newId.setIdCharacteristic(characteristics.getIdCharacteristic());

            ProductCharacteristics newProductCharacteristic = new ProductCharacteristics();
            newProductCharacteristic.setIdProductCharacteristics(newId);
            newProductCharacteristic.setProduct(product);
            newProductCharacteristic.setCharacteristic(characteristics);
            if (request.getValueProduct() != null) {
                newProductCharacteristic.setValueProduct(request.getValueProduct());
            } else {
                newProductCharacteristic.setValueProduct(productCharacteristics.getValueProduct());

            }
            productCharacteristicsRepository.save(newProductCharacteristic);

        }

          productRepository.saveAndFlush(product);
        logger.debug("Product after changes: name={}, description={}, price={}, category={}", product.getNameProduct(),
                product.getDescription(), product.getPrice(), product.getProductCategory());
    }

    @Transactional
    public void deleteProduct(Long idProduct) {
        Product product = productRepository.findById(idProduct).orElseThrow(() -> {
            logger.warn("Product with ID={} not found to delete" , idProduct);
            return new NotFoundException("Product not found");
        });
        List<Inventory> inventories = inventoryRepository.findByProduct_IdProduct(idProduct);

        product.setIsDeleted((byte) 1);
        for (Inventory inventory: inventories ) {
            inventoryRepository.delete(inventory);
        }
    }



}
