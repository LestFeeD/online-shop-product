package com.shopir.product.service;

import com.shopir.product.dto.requestDto.NewSupplyGoodsRequestDto;
import com.shopir.product.dto.responseDto.SupplyGoodsResponseDto;
import com.shopir.product.entity.IdSupplyGoods;
import com.shopir.product.entity.Product;
import com.shopir.product.entity.Supplier;
import com.shopir.product.entity.SupplyGoods;
import com.shopir.product.exceptions.NotFoundException;
import com.shopir.product.factories.SupplyGoodsFactory;
import com.shopir.product.repository.ProductRepository;
import com.shopir.product.repository.SupplierRepository;
import com.shopir.product.repository.SupplyGoodsRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplyGoodsService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final SupplyGoodsRepository supplyGoodsRepository;
    private final SupplyGoodsFactory supplyGoodsFactory;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SupplyGoodsService.class);

    @Autowired
    public SupplyGoodsService(ProductRepository productRepository, SupplierRepository supplierRepository, SupplyGoodsRepository supplyGoodsRepository, SupplyGoodsFactory supplyGoodsFactory) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.supplyGoodsRepository = supplyGoodsRepository;
        this.supplyGoodsFactory = supplyGoodsFactory;
    }

    @Cacheable(value = "supply_goods")
    @Transactional(readOnly = true)
    public List<SupplyGoodsResponseDto> findAllSupplyGoods() {
        List<SupplyGoods> supplyGoods = supplyGoodsRepository.findAll();

        return supplyGoods.stream()
                .map(supplyGoodsFactory::makeSupplyGoodsDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SupplyGoodsResponseDto> findSupplyGoodsByDate(Date startDate, Date endDate) {
        List<SupplyGoods> supplyGoods;
        if(endDate != null) {
            supplyGoods = supplyGoodsRepository.findByDateSupplyBetween(startDate, endDate);
        } else {
            supplyGoods = supplyGoodsRepository.findByDateSupply(startDate);
        }
        return supplyGoods.stream()
                .map(supplyGoodsFactory::makeSupplyGoodsDto)
                .toList();
    }

    @CacheEvict(value = "supply_goods")
    @Transactional
    public void addSupplyGoods(NewSupplyGoodsRequestDto request) {
        logger.debug("Receiving data from request: idSupplier={}, idProduct={}, totalCost={}", request.getIdSupplier(), request.getIdProduct(), request.getTotalCost());

        Supplier supplier = supplierRepository.findById(request.getIdSupplier()).orElseThrow( () -> {
                logger.warn("Supplier with ID={} not found", request.getIdSupplier());
        return new NotFoundException("Supplier not found");
        });
        Product product = productRepository.findById(request.getIdProduct()).orElseThrow(() -> {
            logger.warn("Product with ID={} not found", request.getIdProduct());
            return new NotFoundException("Supplier not found");
        });
        IdSupplyGoods idSupplyGoods = new IdSupplyGoods();
        idSupplyGoods.setIdProduct(request.getIdProduct());
        idSupplyGoods.setIdSupplier(request.getIdSupplier());
        SupplyGoods supplyGoods = SupplyGoods.builder()
                .idSupplyGoods(idSupplyGoods)
                .supplier(supplier)
                .product(product)
                .totalCost(request.getTotalCost())
                .build();
        supplyGoodsRepository.save(supplyGoods);
    }

    public BigDecimal deliveryCost(Date startDate, Date endDate) {
        List<SupplyGoods> supplyGoods;
        BigDecimal cost =  BigDecimal.ZERO;
        if(endDate != null) {
            supplyGoods = supplyGoodsRepository.findByDateSupplyBetween(startDate, endDate);
        } else {
            supplyGoods = supplyGoodsRepository.findByDateSupply(startDate);
        }
        for(SupplyGoods supplyGoodsOne: supplyGoods) {
            cost = cost.add(supplyGoodsOne.getTotalCost());
        }
        return cost;
    }


}
