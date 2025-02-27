package com.example.inventoryservice.controller;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.product.ProductFilter;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import com.example.inventoryservice.mapper.CategoryMapper;
import com.example.inventoryservice.mapper.ProductMapper;
import com.example.inventoryservice.mapper.ProductMapperDecorator;
import com.example.inventoryservice.service.CategoryService;
import com.example.inventoryservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @Configuration
    @ComponentScan(basePackageClasses = ProductController.class)
    public static class TestConf {
    }

    private Product product;

    private List<Product> products;

    @BeforeEach
    public void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setTitle("any category");

        product = new Product();
        product.setTitle("some product");
        product.setDescription("description");
        product.setUnitPrice(BigDecimal.ONE);
        product.setCount(100);
        product.setCategory(category);

        products = Collections.singletonList(product);
    }

    @Test
    public void getProductById() throws Exception {
        when(productService.getById(1L)).thenReturn(product);
        mockMvc.perform(get("/product/view/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(product.getTitle())));
    }

    @Test
    public void getAllProducts() throws Exception {
        when(productService.getAll(new ProductFilter(), PageRequest.of(0, 1))).thenReturn(new PageImpl<>(products));
        mockMvc.perform(
                        get("/product/view")
                                .param("page", "0")
                                .param("size", "1")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(product.getTitle())));
    }

    @Test
    public void createProduct() throws Exception {
        UpsertProductRequest request = new UpsertProductRequest();
        request.setTitle("new product");
        request.setDescription("description");
        request.setUnitPrice(BigDecimal.ONE);
        request.setCount(128);
        request.setCategoryTitle("any category");

        mockMvc.perform(
                        post("/product/add")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void updateProduct() throws Exception {
        UpsertProductRequest request = new UpsertProductRequest();
        request.setTitle("changed product");
        request.setDescription("description");
        request.setUnitPrice(BigDecimal.ONE);
        request.setCount(128);
        request.setCategoryTitle("any category");
        when(productService.getById(1L)).thenReturn(product);
        mockMvc.perform(
                        put("/product/update/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProductById() throws Exception {
        doNothing().when(productService).deleteById(1L);
        mockMvc.perform(delete("/product/delete/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
