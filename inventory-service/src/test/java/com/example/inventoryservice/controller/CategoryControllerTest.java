package com.example.inventoryservice.controller;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.mapper.CategoryMapper;
import com.example.inventoryservice.mapper.ProductMapper;
import com.example.inventoryservice.service.CategoryService;
import com.example.inventoryservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
@WebMvcTest(value = CategoryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @Configuration
    @ComponentScan(basePackageClasses = CategoryController.class)
    public static class TestConf {
    }

    private Category category;

    private List<Category> categories;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setTitle("any category");

        categories = Collections.singletonList(category);
    }

    @Test
    public void getCategoryById() throws Exception {
        when(categoryService.getById(1L)).thenReturn(category);
        mockMvc.perform(get("/category/view/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(category.getTitle())));
    }

    @Test
    public void getAllCategories() throws Exception {
        when(categoryService.getAll()).thenReturn(categories);
        mockMvc.perform(get("/category/view"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(category.getTitle())));
    }

    @Test
    public void createCategory() throws Exception {
        mockMvc.perform(
                        post("/category/add")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"title\": \"new category\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void updateCategory() throws Exception {
        when(categoryService.getById(1L)).thenReturn(category);
        mockMvc.perform(
                        put("/category/update/1")
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{\"title\": \"changed category\"}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCategoryById() throws Exception {
        doNothing().when(categoryService).deleteById(1L);
        mockMvc.perform(delete("/category/delete/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}