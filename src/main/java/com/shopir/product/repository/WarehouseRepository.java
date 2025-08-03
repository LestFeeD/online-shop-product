package com.shopir.product.repository;

import com.shopir.product.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
