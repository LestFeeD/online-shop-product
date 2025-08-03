package com.shopir.product.repository;

import com.shopir.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByNameProductIgnoreCaseAndIsDeleted(String name, Byte isDeleted);
    List<Product> findByNameProductContainingIgnoreCaseAndIsDeletedIsNull(String partialName);
    List<Product> findAllByIsDeleted(Byte isDeleted);

    @Query(value = "SELECT p.* \n" +
            "\tFROM product p " +
            "JOIN cart c ON c.id_product = p.id_product " +
            "WHERE c.id_cart = :idCart ", nativeQuery = true)
    Product findByIdCart(@Param(value = "idCart") Long idCart);

    @Query(value = "SELECT p.id_product,  p.description, p.price, p.id_product_category, p.is_deleted, p.name_product \n" +
            "\tFROM product p " +
            "JOIN order_product op ON op.id_product = p.id_product " +
            "JOIN order_user ou ON ou.id_order = op.id_order " +
            "WHERE ou.id_order = :idOrder", nativeQuery = true)
    Optional<List<Product>> findByIdOrder(Long idOrder);


    @Query(value = "SELECT DISTINCT  p.id_product,  p.description, p.price, p.id_product_category, p.is_deleted, p.name_product, pc.name, \n" +
            "ppc.value_product, c.name FROM product p \n" +
            "JOIN product_category pc ON pc.id_product_category = p.id_product_category\n" +
            "JOIN product_characteristics ppc ON ppc.id_product = p.id_product\n" +
            "JOIN characteristic c ON c.id_characteristic = ppc.id_characteristic\n" +
            "JOIN order_product op ON op.id_product = p.id_product\n" +
            "JOIN order_user ou ON ou.id_order = op.id_order\n" +
            "WHERE p.is_deleted = 0\n" +
            "ORDER BY p.id_product \n" +
            "LIMIT 20; ", nativeQuery = true)
    List<Product> findMostPopularProducts();

    Optional<Product> findByIdProductAndIsDeleted(Long idProduct, Byte isDeleted);

}
