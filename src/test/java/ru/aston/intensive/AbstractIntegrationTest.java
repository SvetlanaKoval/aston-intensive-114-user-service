package ru.aston.intensive;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.repository.UserRepository;

@SpringBootTest
@Testcontainers
public abstract class AbstractIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.1-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Autowired
    protected UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

}
