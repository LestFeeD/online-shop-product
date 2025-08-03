package com.shopir.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopir.product.dto.ProductKafkaDto;
import com.shopir.product.entity.Product;
import com.shopir.product.exceptions.NotFoundException;
import com.shopir.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KafkaProducerService {
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    public KafkaProducerService(ProductRepository productRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "product-request", groupId = "product-group")
    public void handleProductByIdRequest(String idProduct) throws JsonProcessingException {
        logger.debug("Received product id={}", idProduct);
        Long id = Long.parseLong(idProduct);
        Product product = productRepository.findById(id).orElseThrow();
        ProductKafkaDto dto = ProductKafkaDto.builder()
                .idProduct(product.getIdProduct())
                .nameProduct(product.getNameProduct())
                .price(product.getPrice())
                .build();

        String json = objectMapper.writeValueAsString(dto);

        kafkaTemplate.send("product-response", json);

    }

    @KafkaListener(topics = "cart-product-request", groupId = "product-group")
    public void handleProductByCartIdRequest(String idCart) throws JsonProcessingException {
        logger.debug("Received idCart id={}", idCart);
        Long id = Long.parseLong(idCart);
        Product product = productRepository.findByIdCart(id);
        ProductKafkaDto dto = ProductKafkaDto.builder()
                .idProduct(product.getIdProduct())
                .nameProduct(product.getNameProduct())
                .price(product.getPrice())
                .idCart(id)
                .build();

        String json = objectMapper.writeValueAsString(dto);

        kafkaTemplate.send("cart-product-response", json);

    }

    @KafkaListener(topics = "cart-idProduct-request", groupId = "product-group")
    public void handleIdProductRequest(String idCart) throws JsonProcessingException {
        Long id = Long.parseLong(idCart);
        Product product = productRepository.findByIdCart(id);

        kafkaTemplate.send("idProduct-response", product.getIdProduct().toString());
    }

    @KafkaListener(topics = "order-product-request", groupId = "user-group")
    public void handleProductByIdOrderRequest(String idOrder) throws JsonProcessingException {
        logger.debug("Received idOrder id={}", idOrder);
        Long id = Long.parseLong(idOrder);
        Optional<List<Product>> optionalProducts = Optional.ofNullable(productRepository.findByIdOrder(id).orElseThrow(() -> new NotFoundException("Not found this id: " + idOrder)));
        List<Product> products = optionalProducts.get();
        List<ProductKafkaDto> productKafkaDtos = new ArrayList<>();
        for (Product product: products) {
            ProductKafkaDto dto = ProductKafkaDto.builder()
                    .idProduct(product.getIdProduct())
                    .nameProduct(product.getNameProduct())
                    .price(product.getPrice())
                    .idOrder(id)
                    .build();
            productKafkaDtos.add(dto);
        }
        String json = objectMapper.writeValueAsString(productKafkaDtos);

        kafkaTemplate.send("order-product-response", json);

    }
}
