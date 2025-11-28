package com.orderapp.customerservice.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.orderapp.customerservice.repository.shareddb", // Quét Repo Shared
        entityManagerFactoryRef = "sharedEntityManagerFactory",
        transactionManagerRef = "sharedTransactionManager"
)
public class SharedDbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.shared")
    public DataSourceProperties sharedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource sharedDataSource() {
        return sharedDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "sharedEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sharedEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sharedDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.orderapp.customerservice.entity.shareddb") // Quét Entity Shared
                .persistenceUnit("shared")
                .build();
    }

    @Bean(name = "sharedTransactionManager")
    public PlatformTransactionManager sharedTransactionManager(
            @Qualifier("sharedEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}