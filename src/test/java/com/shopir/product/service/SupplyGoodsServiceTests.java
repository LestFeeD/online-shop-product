package com.shopir.product.service;

import com.shopir.product.dto.requestDto.NewSupplyGoodsRequestDto;
import com.shopir.product.dto.responseDto.InventoryResponseDto;
import com.shopir.product.dto.responseDto.SupplyGoodsResponseDto;
import com.shopir.product.entity.Product;
import com.shopir.product.entity.Supplier;
import com.shopir.product.entity.SupplyGoods;
import com.shopir.product.factories.SupplyGoodsFactory;
import com.shopir.product.repository.InventoryRepository;
import com.shopir.product.repository.ProductRepository;
import com.shopir.product.repository.SupplierRepository;
import com.shopir.product.repository.SupplyGoodsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupplyGoodsServiceTests {
    @InjectMocks
    private SupplyGoodsService service;

    @Mock
    private SupplyGoodsRepository supplyGoodsRepository;

    @Mock
    private  SupplyGoodsFactory supplyGoodsFactory;

    @Mock
    private  SupplierRepository supplierRepository;

    @Mock
    private  ProductRepository productRepository;



    @Test
    void findAllSupplyGoods_findAllSupplyGoods_returnListSupplyGoodsResponseDto() {
        List<SupplyGoods> supplyGoods = Mockito.mock();

        when(supplyGoodsRepository.findAll()).thenReturn(supplyGoods);
        service.findAllSupplyGoods();
        verify(supplyGoodsRepository).findAll();
    }

    @Test
    void findSupplyGoodsByDate_findAllSupplyGoodsBetweenDates_returnListSupplyGoodsResponseDto() {
        List<SupplyGoods> supplyGoods = Mockito.mock();
        Date startDate =  Date.valueOf("2024-02-02");
        Date endDate =  Date.valueOf("2025-02-02");

        when(supplyGoodsRepository.findByDateSupplyBetween(startDate,endDate )).thenReturn(supplyGoods);
        service.findSupplyGoodsByDate(startDate, endDate);
        verify(supplyGoodsRepository).findByDateSupplyBetween(startDate,endDate);
    }

    @Test
    void findSupplyGoodsByDate_findAllSupplyGoodsOneDate_returnListSupplyGoodsResponseDto() {
        List<SupplyGoods> supplyGoods = Mockito.mock();
        Date startDate =  Date.valueOf("2024-02-02");

        when(supplyGoodsRepository.findByDateSupply(startDate )).thenReturn(supplyGoods);
        service.findSupplyGoodsByDate(startDate, null);
        verify(supplyGoodsRepository).findByDateSupply(startDate);
    }

    @Test
    void addSupplyGoods_addNewSupplyGoods() {
        NewSupplyGoodsRequestDto requestDto = NewSupplyGoodsRequestDto.builder()
                .idProduct(1L)
                .idSupplier(1L)
                .totalCost(BigDecimal.valueOf(220))
                .build();
        Supplier supplier = Mockito.mock();
        Product product = Mockito.mock();
        when(supplierRepository.findById(1L)).thenReturn(Optional.ofNullable(supplier));
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));

        service.addSupplyGoods(requestDto);
        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(1L);

    }

    @Test
    void deliveryCost_getDeliveryCostBetweenDates() {

        SupplyGoods supplyGoods1 = Mockito.mock();
        List<SupplyGoods> supplyGoods = List.of(supplyGoods1);

        Date startDate =  Date.valueOf("2024-02-02");
        Date endDate =  Date.valueOf("2025-02-02");

        when(supplyGoodsRepository.findByDateSupplyBetween(startDate, endDate)).thenReturn(supplyGoods);
        when(supplyGoods1.getTotalCost()).thenReturn(BigDecimal.valueOf(2));

        service.deliveryCost(startDate,endDate );
        verify(supplyGoodsRepository).findByDateSupplyBetween(startDate, endDate);

    }

    @Test
    void deliveryCost_getDeliveryCostOneDate() {

        SupplyGoods supplyGoods1 = Mockito.mock();
        List<SupplyGoods> supplyGoods = List.of(supplyGoods1);
        Date startDate =  Date.valueOf("2024-02-02");

        when(supplyGoodsRepository.findByDateSupply(startDate)).thenReturn(supplyGoods);
        when(supplyGoods1.getTotalCost()).thenReturn(BigDecimal.valueOf(2));

        service.deliveryCost(startDate,null );
        verify(supplyGoodsRepository).findByDateSupply(startDate);

    }
}
