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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createProductSuccess() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Product 1");
        productRequest.setDescription("This is Product 1");
        productRequest.setPrice(BigDecimal.valueOf(1000000));

        mockMvc.perform(
                post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
//            HttpResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
//                    new TypeReference<>() {
//                    });
//
//            Product product = productRepository.findById(response.body().getId()).orElse(null);
//
//            assertEquals(productRequest.getName(), product.getName());
//            assertEquals(productRequest.getDescription(), product.getDescription());
//            assertEquals(productRequest.getPrice(), product.getPrice());
        });
    }
}
