package com.example.inventoryservice.service.impl;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.dto.category.UpsertCategoryRequest;
import com.example.inventoryservice.exception.EntityNotFoundException;
import com.example.inventoryservice.mapper.CategoryMapper;
import com.example.inventoryservice.repository.CategoryRepository;
import com.example.inventoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format("Category with id {0} not found", id)));
    }

    @Override
    public Category getByTitle(String title){
        return categoryRepository.findByTitle(title)
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format("Category with title {0} not found", title)));
    }

    @Override
    public Category create(UpsertCategoryRequest request) {
        Category newCategory = new Category();
        newCategory.setTitle(request.getTitle());
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category update(Long id, UpsertCategoryRequest request) {
        Category updatedCategory = getById(id);
        CategoryMapper.INSTANCE.update(request, updatedCategory);
        return categoryRepository.save(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {categoryRepository.deleteById(id);}
}
