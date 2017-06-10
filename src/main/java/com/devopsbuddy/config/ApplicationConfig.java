package com.devopsbuddy.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration valid to all profiles.
 * Created by root on 10/06/17.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.devopsbuddy.backend.persistence.repositories") // Scan all JPA repositories
@EntityScan(basePackages = "com.devopsbuddy.backend.persistence.domain.backend") // Scan all JPA entities
@EnableTransactionManagement // This enables annotation based transaction management (JPA transaction is managed by default)
public class ApplicationConfig {
}
