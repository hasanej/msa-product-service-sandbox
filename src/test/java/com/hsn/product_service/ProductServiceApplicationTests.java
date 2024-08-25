package com.hsn.product_service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsn.product_service.dto.ProductRequest;
import com.hsn.product_service.dto.ProductResponse;
import com.hsn.product_service.model.Product;
import com.hsn.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createProductSuccess() throws Exception {
        ProductRequest productRequest = setProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/api/product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productRequestString)
                )
                .andExpect(
                        status().isCreated()
                )
                .andDo(result -> {
                    ProductResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    // Find the created product
                    Product product = productRepository.findById(response.getId()).orElse(null);

                    assertEquals(productRequest.getName(), product.getName());
                    assertEquals(productRequest.getDescription(), product.getDescription());
                    assertEquals(productRequest.getPrice(), product.getPrice());
                });
    }

    @Test
    void getAllProductsSuccess() throws Exception {
        insertProducts();

        mockMvc.perform(get("/api/product"))
                .andExpect(
                        status().isOk()
                )
                .andDo(result -> {
                    List<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });

                    assertNotNull(response);
                    assertNotEquals(0, response.size());
                    assertEquals(10, response.size());
                });
    }

    private ProductRequest setProductRequest() {
        return ProductRequest.builder()
                .name("Product 1")
                .description("This is Product 1")
                .price(BigDecimal.valueOf(1000000))
                .build();
    }

    private void insertProducts() {
        // Insert 10 products
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("This is Product " + i);
            product.setPrice(BigDecimal.valueOf(1000000 + i));
            productRepository.save(product);
        }
    }
}
