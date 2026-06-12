package com.clothingstore.crm.service.impl;

import com.clothingstore.crm.dto.ProductDto;
import com.clothingstore.crm.entity.Category;
import com.clothingstore.crm.entity.Product;
import com.clothingstore.crm.exception.ResourceNotFoundException;
import com.clothingstore.crm.repository.CategoryRepository;
import com.clothingstore.crm.repository.ProductRepository;
import com.clothingstore.crm.service.AuditService;
import com.clothingstore.crm.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> search(String query, Long categoryId, Pageable pageable) {
        String q = (query == null || query.isBlank()) ? null : query.trim();
        return productRepository.search(q, categoryId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        Product p = new Product();
        apply(p, dto);
        p = productRepository.save(p);
        auditService.log("CREATE", "Product", p.getId(), "Created product " + p.getName());
        return toDto(p);
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product p = findOrThrow(id);
        apply(p, dto);
        p = productRepository.save(p);
        auditService.log("UPDATE", "Product", p.getId(), "Updated product " + p.getName());
        return toDto(p);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product p = findOrThrow(id);
        productRepository.delete(p);
        auditService.log("DELETE", "Product", id, "Deleted product " + p.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> lowStock() {
        return productRepository.findLowStock().stream().map(this::toDto).toList();
    }

    private void apply(Product p, ProductDto dto) {
        p.setName(dto.name());
        p.setSize(dto.size());
        p.setColor(dto.color());
        p.setPrice(dto.price());
        p.setStockQuantity(dto.stockQuantity());
        if (dto.lowStockThreshold() != null) p.setLowStockThreshold(dto.lowStockThreshold());
        p.setDescription(dto.description());
        p.setImageUrl(dto.imageUrl());
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", dto.categoryId()));
            p.setCategory(category);
        } else {
            p.setCategory(null);
        }
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private ProductDto toDto(Product p) {
        return new ProductDto(p.getId(), p.getName(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getSize(), p.getColor(), p.getPrice(), p.getStockQuantity(),
                p.getLowStockThreshold(), p.getDescription(), p.getImageUrl());
    }
}
