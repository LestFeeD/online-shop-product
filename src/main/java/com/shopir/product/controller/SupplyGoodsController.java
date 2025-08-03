package com.shopir.product.controller;

import com.shopir.product.dto.requestDto.NewSupplyGoodsRequestDto;
import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.dto.responseDto.SupplyGoodsResponseDto;
import com.shopir.product.service.SupplyGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
public class SupplyGoodsController {
    private final SupplyGoodsService service;

    @Autowired
    public SupplyGoodsController(SupplyGoodsService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @GetMapping("/supply-goods")
    public ResponseEntity<List<SupplyGoodsResponseDto>> findAllSupplyGoods(@PathVariable("idProduct") Long idProduct) {
        List<SupplyGoodsResponseDto> product =  service.findAllSupplyGoods();
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @GetMapping("/supply-goods/date")
    public ResponseEntity<List<SupplyGoodsResponseDto>> findSupplyGoodsByDate(@RequestParam("startDate") Date startDate, @RequestParam("endDate") Date endDate) {
        List<SupplyGoodsResponseDto> supplyGoods =  service.findSupplyGoodsByDate(startDate, endDate);
        return ResponseEntity.ok(supplyGoods);

    }

    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @PostMapping("/supply-goods")
    public ResponseEntity<Void> addSupplyGoods(@RequestBody NewSupplyGoodsRequestDto requestDto ) {
        service.addSupplyGoods(requestDto);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN', 'MANAGER' )")
    @GetMapping("/devility-cost")
    public ResponseEntity<BigDecimal> devilityCost(@RequestParam("startDate") Date startDate, @RequestParam("endDate") Date endDate) {
        BigDecimal cost =  service.deliveryCost(startDate, endDate);
        return ResponseEntity.ok(cost);

    }
}
