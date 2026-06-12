package com.clothingstore.crm.controller;

import com.clothingstore.crm.dto.ApiResponse;
import com.clothingstore.crm.dto.CategoryDto;
import com.clothingstore.crm.entity.Category;
import com.clothingstore.crm.exception.ResourceNotFoundException;
import com.clothingstore.crm.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ApiResponse<List<CategoryDto>> list() {
        return ApiResponse.ok(categoryRepository.findAll().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription()))
                .toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','SALES_MANAGER')")
    @PostMapping
    public ApiResponse<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        Category c = categoryRepository.save(Category.builder()
                .name(dto.name()).description(dto.description()).build());
        return ApiResponse.ok("Category created", new CategoryDto(c.getId(), c.getName(), c.getDescription()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        categoryRepository.delete(c);
        return ApiResponse.ok("Category deleted", null);
    }
}
