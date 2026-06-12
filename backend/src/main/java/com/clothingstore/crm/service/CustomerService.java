package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.CustomerDto;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<CustomerDto> search(String query, CustomerStatus status, Pageable pageable);
    CustomerDto getById(Long id);
    CustomerDto create(CustomerDto dto);
    CustomerDto update(Long id, CustomerDto dto);
    void delete(Long id);
    Page<OrderDto> purchaseHistory(Long customerId, Pageable pageable);
    String exportCsv();
}
