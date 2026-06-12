package com.clothingstore.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application entry point.
 * {@code @EnableJpaAuditing} powers automatic createdAt / updatedAt timestamps
 * on entities annotated with {@code @CreatedDate} / {@code @LastModifiedDate}.
 */
@SpringBootApplication
@EnableJpaAuditing
public class CrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }
}
