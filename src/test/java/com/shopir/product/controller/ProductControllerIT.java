package com.shopir.product.controller;

import com.shopir.product.dto.UserKafkaDto;
import com.shopir.product.dto.requestDto.EditProductRequest;
import com.shopir.product.dto.requestDto.NewProductRequest;
import com.shopir.product.dto.responseDto.ProductResponseDto;
import com.shopir.product.security.MyUserDetails;
import com.shopir.product.service.KafkaConsumerService;
import com.shopir.product.service.UserService;
import com.shopir.product.utils.JwtUtils;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
import org.testcontainers.shaded.com.google.common.util.concurrent.ListenableFuture;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@EmbeddedKafka(partitions = 1, controlledShutdown = true)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIT {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }

    private static Long categoryId;
    private static Long characteristicId;
    private static Long secondCharacteristicId;
    private static Long warehouseId;
    @Autowired
    private JwtUtils jwtUtils;
    @Mock
    private UserService customUserDetailsService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;


    @BeforeAll
    static void setUp() throws Exception {
        // Настройка Liquibase
        Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        // Путь к changelog-файлу (должен быть в resources)
        Liquibase liquibase = new Liquibase("db/changelog/main-changelog.xml",
                new ClassLoaderResourceAccessor(), database);

        // Применяем миграции
        liquibase.update(new Contexts(), new LabelExpression());
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_product_category FROM product_category WHERE name = 'Электроника' ORDER BY id_product_category DESC LIMIT 1"
        )) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    categoryId = rs.getLong(1);
                    System.out.println("Inserted categoryId = " + categoryId);
                }
            }
        }

        // Получаем id для characteristic (для 'Мгц')
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_characteristic FROM characteristic WHERE name = 'Мгц' ORDER BY id_characteristic DESC LIMIT 1"
        )) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    characteristicId = rs.getLong(1);
                    System.out.println("Inserted characteristicId = " + characteristicId);
                }
            }
        }

        // Получаем id для второй characteristic (для 'Гб')
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_characteristic FROM characteristic WHERE name = 'ГЦ' ORDER BY id_characteristic DESC LIMIT 1"
        )) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    secondCharacteristicId = rs.getLong(1);
                    System.out.println("Inserted secondCharacteristicId = " + secondCharacteristicId);
                }
            }
        }

        // Получаем id для warehouse
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id_warehouse FROM warehouse WHERE warehouse_number = 323 ORDER BY id_warehouse DESC LIMIT 1"
        )) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    warehouseId = rs.getLong(1);
                    System.out.println("Inserted warehouseId = " + warehouseId);
                }
            }
        }
    }


    @Test
    void canEstablishedConnection() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void findProductById() {
    }

    @Test
    void findAllProduct() {

        NewProductRequest newProductRequest = NewProductRequest.builder()
                .name("test1")
                .description("testDesc")
                .price(BigDecimal.valueOf(22))
                .idProductCategory(categoryId)
                .idWarehouse(List.of(warehouseId))
                .idCharacteristics(List.of(characteristicId))
                .valueProduct(List.of(1))
                .quantitySet(List.of(1L))
                .build();

        NewProductRequest newProductRequest1 = NewProductRequest.builder()
                .name("test2")
                .description("testDesc")
                .price(BigDecimal.valueOf(22))
                .idProductCategory(categoryId)
                .idWarehouse(List.of(warehouseId))
                .idCharacteristics(List.of(characteristicId))
                .valueProduct(List.of(1))
                .quantitySet(List.of(1L))
                .build();

        webTestClient.post()
                .uri("/product")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(newProductRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        webTestClient.post()
                .uri("/product")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(newProductRequest1)
                .exchange()
                .expectStatus()
                .isCreated();

        var findProducts = webTestClient.get()
                .uri("/search")
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(findProducts);
        ProductResponseDto productCreated1 = findProducts.stream()
                .filter(product -> product != null && newProductRequest.getName().equals(product.getNameProduct()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product 'test1' was not created"));

        ProductResponseDto productCreated2 = findProducts.stream()
                .filter(product -> product != null && newProductRequest1.getName().equals(product.getNameProduct()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product 'test2' was not created"));


        assertThat(productCreated1.getNameProduct()).isEqualTo(newProductRequest.getName());
        assertThat(productCreated2.getNameProduct()).isEqualTo(newProductRequest1.getName());

    }

    @Test
    void findProduct() {
    }

    @Test
    void suggestProductsByName() {
    }

    @Test
    void addNewProduct() throws Exception {

        NewProductRequest newProductRequest = NewProductRequest.builder()
                .name("test1")
                .description("testDesc")
                .price(BigDecimal.valueOf(22))
                .idProductCategory(categoryId)
                .idWarehouse(List.of(warehouseId))
                .idCharacteristics(List.of(characteristicId))
                .valueProduct(List.of(1))
                .quantitySet(List.of(1L))
                .build();
        MyUserDetails userDetails = new MyUserDetails(2L, "last","1111",  List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtUtils.generateToken(authentication);

        kafkaTemplate.send("product-topic", "last");

        UserKafkaDto userKafkaDto = kafkaConsumerService.getInfoUserByEmail("last");


        String mockBearerToken = "Bearer " + accessToken;
        webTestClient.post()
                .uri("/product")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(newProductRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        ProductResponseDto  findProduct = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search-product")
                        .queryParam("nameProduct", "test1")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(findProduct).isNotNull();
        assertThat(findProduct.getNameProduct()).isEqualTo(newProductRequest.getName());
    }

    @Test
    void editProduct() throws Exception {
        NewProductRequest newProductRequest = NewProductRequest.builder()
                .name("test1")
                .description("testDesc")
                .price(BigDecimal.valueOf(22))
                .idProductCategory(categoryId)
                .idWarehouse(List.of(warehouseId))
                .idCharacteristics(List.of(characteristicId))
                .valueProduct(List.of(1))
                .quantitySet(List.of(1L))
                .build();

        MyUserDetails userDetails = new MyUserDetails(2L, "last","1111",  List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtUtils.generateToken(authentication);

        kafkaTemplate.send("product-topic", "last");

        UserKafkaDto userKafkaDto = kafkaConsumerService.getInfoUserByEmail("last");


        String mockBearerToken = "Bearer " + accessToken;

        webTestClient.post()
                .uri("/product")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(newProductRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        ProductResponseDto  findProduct = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search-product")
                        .queryParam("nameProduct", "test1")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(findProduct);
        System.out.println("Id findProduct " + findProduct.getIdProduct());

        EditProductRequest request = new EditProductRequest();
        request.setName("newTaskName");
        request.setIdCharacteristic(secondCharacteristicId);
        request.setOldIdCharacteristic(characteristicId);
        request.setValueProduct(1);
        Assertions.assertNotNull(findProduct);


        webTestClient.patch()
                .uri("/product/" + findProduct.getIdProduct())
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        var updatedProduct = webTestClient.get()
                .uri(String.format("/product/" + findProduct.getIdProduct()))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(updatedProduct);
        String onlyCharacteristic = updatedProduct.getNameCharacteristics()
                .stream()
                .filter(p -> p.equals("ГЦ"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No characteristic found"));

        assertThat(updatedProduct.getNameProduct()).isEqualTo(request.getName());
        assertThat(onlyCharacteristic).isEqualTo("ГЦ");

    }

    @Test
    void deleteProduct() throws Exception {
        NewProductRequest newProductRequest = NewProductRequest.builder()
                .name("test1")
                .description("testDesc")
                .price(BigDecimal.valueOf(22))
                .idProductCategory(categoryId)
                .idWarehouse(List.of(warehouseId))
                .idCharacteristics(List.of(characteristicId))
                .valueProduct(List.of(1))
                .quantitySet(List.of(1L))
                .build();

        MyUserDetails userDetails = new MyUserDetails(2L, "last","1111",  List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtUtils.generateToken(authentication);

        kafkaTemplate.send("product-topic", "last");

        UserKafkaDto userKafkaDto = kafkaConsumerService.getInfoUserByEmail("last");


        String mockBearerToken = "Bearer " + accessToken;

        webTestClient.post()
                .uri("/product")
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .bodyValue(newProductRequest)
                .exchange()
                .expectStatus()
                .isCreated();

        ProductResponseDto  findProduct = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search-product")
                        .queryParam("nameProduct", "test1")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertNotNull(findProduct);
        System.out.println("Id findProduct " + findProduct.getIdProduct());

        webTestClient.delete()
                .uri("/product/" +  findProduct.getIdProduct())
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNoContent();

        var deletedProject = webTestClient.get()
                .uri(String.format("/product/" + findProduct.getIdProduct()))
                .header(HttpHeaders.AUTHORIZATION, mockBearerToken)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
//List<ProductResponseDto> findProducts = webTestClient.get()
//        .uri(uriBuilder -> uriBuilder
//                .path("/search-product")
//                .queryParam("name", "test1")
//                .build())
//        .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
//        .exchange()
//        .expectStatus()
//        .isOk()
//        .expectBodyList(ProductResponseDto.class)
//        .returnResult()
//        .getResponseBody();
//
//ProductResponseDto productCreated = Objects.requireNonNull(findProducts)
//        .stream()
//        .filter(product -> product != null && newProductRequest.getName().equals(product.getNameProduct()))
//        .findFirst()
//        .orElseThrow(() -> new AssertionError("Product was not created"));



   /* @BeforeAll
    static void setupDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            conn.setAutoCommit(false);
            // Вставка product_category
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO product_category(name) VALUES (?) RETURNING id_product_category")) {
                stmt.setString(1, "Электроника");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    categoryId = rs.getLong(1);
                }
            }

            // Вставка characteristic
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO characteristic(name) VALUES (?) RETURNING id_characteristic")) {
                stmt.setString(1, "Мгц");
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    characteristicId = rs.getLong(1);
                    System.out.println("Inserted characteristicId = " + characteristicId);
                }
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "INSERT INTO characteristic(name) VALUES (?) RETURNING id_characteristic")) {
                stmt2.setString(1, "Гб");
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    secondCharacteristicId = rs2.getLong(1);
                    System.out.println("Inserted characteristicId 2 = " + secondCharacteristicId);
                }
            }

            // Вставка warehouse
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO warehouse(warehouse_number) VALUES (?) RETURNING id_warehouse")) {
                stmt.setInt(1, 323);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    warehouseId = rs.getLong(1);
                }
            }
            conn.commit();
        }
    }*/
}
