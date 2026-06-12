package com.clothingstore.crm.service.impl;

import com.clothingstore.crm.dto.CustomerDto;
import com.clothingstore.crm.dto.order.OrderDto;
import com.clothingstore.crm.entity.Customer;
import com.clothingstore.crm.entity.CustomerStatus;
import com.clothingstore.crm.exception.BadRequestException;
import com.clothingstore.crm.exception.ResourceNotFoundException;
import com.clothingstore.crm.repository.CustomerRepository;
import com.clothingstore.crm.repository.OrderRepository;
import com.clothingstore.crm.service.AuditService;
import com.clothingstore.crm.service.CustomerService;
import com.clothingstore.crm.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDto> search(String query, CustomerStatus status, Pageable pageable) {
        String q = (query == null || query.isBlank()) ? null : query.trim();
        return customerRepository.search(q, status, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public CustomerDto create(CustomerDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("A customer with this email already exists");
        }
        Customer c = Customer.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .address(dto.address())
                .status(dto.status() != null ? dto.status() : CustomerStatus.ACTIVE)
                .build();
        c = customerRepository.save(c);
        auditService.log("CREATE", "Customer", c.getId(), "Created customer " + c.getFullName());
        return toDto(c);
    }

    @Override
    @Transactional
    public CustomerDto update(Long id, CustomerDto dto) {
        Customer c = findOrThrow(id);
        c.setFullName(dto.fullName());
        c.setEmail(dto.email());
        c.setPhoneNumber(dto.phoneNumber());
        c.setAddress(dto.address());
        if (dto.status() != null) c.setStatus(dto.status());
        c = customerRepository.save(c);
        auditService.log("UPDATE", "Customer", c.getId(), "Updated customer " + c.getFullName());
        return toDto(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer c = findOrThrow(id);
        customerRepository.delete(c);
        auditService.log("DELETE", "Customer", id, "Deleted customer " + c.getFullName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> purchaseHistory(Long customerId, Pageable pageable) {
        findOrThrow(customerId);
        return orderRepository.findByCustomerId(customerId, pageable).map(OrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv() {
        StringBuilder sb = new StringBuilder("ID,Full Name,Email,Phone,Address,Registration Date,Total Purchases,Loyalty Points,Status\n");
        for (Customer c : customerRepository.findAll()) {
            sb.append(c.getId()).append(',')
              .append(escape(c.getFullName())).append(',')
              .append(escape(c.getEmail())).append(',')
              .append(escape(c.getPhoneNumber())).append(',')
              .append(escape(c.getAddress())).append(',')
              .append(c.getRegistrationDate()).append(',')
              .append(c.getTotalPurchases()).append(',')
              .append(c.getLoyaltyPoints()).append(',')
              .append(c.getStatus()).append('\n');
        }
        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private Customer findOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(c.getId(), c.getFullName(), c.getEmail(), c.getPhoneNumber(),
                c.getAddress(), c.getRegistrationDate(), c.getTotalPurchases(),
                c.getLoyaltyPoints(), c.getStatus());
    }
}
