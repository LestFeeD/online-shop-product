package com.shopir.product.controller;

import com.shopir.product.dto.requestDto.EditProductRequest;
import com.shopir.product.dto.requestDto.NewProductRequest;
import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.entity.ProductCategory;
import com.shopir.product.service.AuthenticationService;
import com.shopir.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    private final AuthenticationService authenticationService;
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, AuthenticationService authenticationService) {
        this.productService = productService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/product/{idProduct}")
    public ResponseEntity<ProductResponseDto> findProductById(@PathVariable("idProduct") Long idProduct) {
        ProductResponseDto product =  productService.findProductById(idProduct);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity< Map<String, List<ProductResponseDto>>> findAllProduct() {
        Long idUser = authenticationService.getCurrentUserId();
        logger.debug("UserId in method {}", idUser);
        if (idUser == null) {
            logger.debug("UserId not found");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, List<ProductResponseDto>> productList = productService.findAllProduct();
        return ResponseEntity.ok(productList);

    }

    @GetMapping("/popular-product")
    public ResponseEntity<List<ProductResponseDto>> getMostPopularProduct() {
        List<ProductResponseDto> products =  productService.getMostPopularProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search-product")
    public ResponseEntity<ProductResponseDto> findProduct(@RequestParam(value = "nameProduct", required = false) String name, @RequestParam(value = "idProduct", required = false)  Long id) {
        ProductResponseDto product = productService.findProduct(name, id);
        return ResponseEntity.ok(product);

    }

    @GetMapping("/search/suggest")
    public ResponseEntity<List<ProductResponseDto>> suggestProductsByName(@RequestParam("partialName") String partialName) {
        List<ProductResponseDto> productList =  productService.suggestProductsByName(partialName);
        return ResponseEntity.ok(productList);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/product")
    public ResponseEntity<Void> addNewProduct(@RequestBody @Valid NewProductRequest newProductRequest, BindingResult bindingResult) {
        Long idProduct = productService.addNewProduct(newProductRequest, bindingResult);
        URI location = URI.create("/products/" + idProduct);
        return ResponseEntity.created(location).build();

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/product/{idProduct}")
    public ResponseEntity<Void> editProduct(@PathVariable("idProduct") Long idProduct, @RequestBody @Valid EditProductRequest request) {
        productService.editProduct(idProduct, request);
        return ResponseEntity.ok().build();

    }

    @PreAuthorize("hasRole('ADMIN' )")
    @DeleteMapping("/product/{idProduct}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("idProduct") Long idProduct) {
        productService.deleteProduct(idProduct);
        return ResponseEntity.noContent().build();
    }

}
