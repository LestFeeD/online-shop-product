package com.shopir.product.repository;

import com.shopir.product.entity.SupplyGoods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface SupplyGoodsRepository extends JpaRepository<SupplyGoods, Long> {
    List<SupplyGoods> findByDateSupplyBetween(Date startDate, Date endDate);
    List<SupplyGoods> findByDateSupply(Date dateSupply);

}
