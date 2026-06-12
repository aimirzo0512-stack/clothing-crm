package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDto> search(String query, Long categoryId, Pageable pageable);
    ProductDto getById(Long id);
    ProductDto create(ProductDto dto);
    ProductDto update(Long id, ProductDto dto);
    void delete(Long id);
    List<ProductDto> lowStock();
}
