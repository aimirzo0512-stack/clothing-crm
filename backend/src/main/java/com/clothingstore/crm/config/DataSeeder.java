package com.clothingstore.crm.config;

import com.clothingstore.crm.entity.*;
import com.clothingstore.crm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds default users, categories and products on first run so the app is
 * usable immediately. Skipped if users already exist. Disable with the
 * "no-seed" Spring profile.
 */
@Component
@Profile("!no-seed")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.saveAll(List.of(
                user("admin", "admin@store.com", "Admin User", Role.ADMIN),
                user("manager", "manager@store.com", "Sales Manager", Role.SALES_MANAGER),
                user("employee", "employee@store.com", "Store Employee", Role.EMPLOYEE)
        ));

        Category tshirts = categoryRepository.save(Category.builder().name("T-Shirts").description("Casual tops").build());
        Category jeans = categoryRepository.save(Category.builder().name("Jeans").description("Denim trousers").build());
        Category jackets = categoryRepository.save(Category.builder().name("Jackets").description("Outerwear").build());

        productRepository.saveAll(List.of(
                Product.builder().name("Classic White Tee").category(tshirts).size("M").color("White")
                        .price(new BigDecimal("19.99")).stockQuantity(120).lowStockThreshold(20)
                        .description("100% cotton crew neck").build(),
                Product.builder().name("Slim Fit Jeans").category(jeans).size("32").color("Blue")
                        .price(new BigDecimal("49.99")).stockQuantity(8).lowStockThreshold(15)
                        .description("Stretch denim slim fit").build(),
                Product.builder().name("Bomber Jacket").category(jackets).size("L").color("Black")
                        .price(new BigDecimal("89.99")).stockQuantity(40).lowStockThreshold(10)
                        .description("Lightweight bomber").build()
        ));

        customerRepository.saveAll(List.of(
                Customer.builder().fullName("Alice Johnson").email("alice@example.com")
                        .phoneNumber("+1-202-555-0101").address("12 Maple St").status(CustomerStatus.VIP)
                        .totalPurchases(new BigDecimal("540.00")).loyaltyPoints(54).build(),
                Customer.builder().fullName("Bob Smith").email("bob@example.com")
                        .phoneNumber("+1-202-555-0102").address("45 Oak Ave").status(CustomerStatus.ACTIVE).build()
        ));
    }

    private User user(String username, String email, String fullName, Role role) {
        return User.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .password(passwordEncoder.encode("password123"))
                .role(role)
                .enabled(true)
                .build();
    }
}
