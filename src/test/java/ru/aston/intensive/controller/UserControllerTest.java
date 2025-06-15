package ru.aston.intensive.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.repository.UserRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.1-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUserIvan;

    @BeforeEach
    void setUp() {
        testUserIvan = new User();
        testUserIvan.setName("Ivan");
        testUserIvan.setEmail("ivan@test.ru");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testGetUserById() throws Exception {
        User savedUser = userRepository.save(testUserIvan);

        mockMvc.perform(get("/users/" + savedUser.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(testUserIvan.getName()))
            .andExpect(jsonPath("$.email").value(testUserIvan.getEmail()));
    }

    @Test
    void testGetNotExistsUserById() throws Exception {
        mockMvc.perform(get("/users/999"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User with id 999 not found"));
    }

    @Test
    void testSaveNewUser() throws Exception {
        mockMvc.perform(post("/users")
                .param("name", testUserIvan.getName())
                .param("email", testUserIvan.getEmail()))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(testUserIvan.getName()))
            .andExpect(jsonPath("$.email").value(testUserIvan.getEmail()));
    }

    @Test
    void testSaveNewUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/users")
                .param("name", "")
                .param("email", testUserIvan.getEmail()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid user name=, email=ivan@test.ru"));
    }

    @Test
    void testSaveNewUserWithMissingData() throws Exception {
        mockMvc.perform(post("/users")
                .param("name", testUserIvan.getName()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        User savedUser = userRepository.save(testUserIvan);

        mockMvc.perform(post("/users/" + savedUser.getId())
                .param("name", "New_Name")
                .param("email", "New_Email"))
            .andExpect(status().isOk());

        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertEquals("New_Name", updatedUser.getName());
        assertEquals("New_Email", updatedUser.getEmail());
    }

    @Test
    void testUpdateUserWithInvalidData() throws Exception {
        User savedUser = userRepository.save(testUserIvan);

        mockMvc.perform(post("/users/" + savedUser.getId())
                .param("name", "")
                .param("email", testUserIvan.getEmail()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid user name=, email=ivan@test.ru"));
    }

    @Test
    void testUpdateNotExistsUser() throws Exception {
        mockMvc.perform(post("/users/999")
                .param("name", testUserIvan.getName())
                .param("email", testUserIvan.getEmail()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User with id 999 not found"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User savedUser = userRepository.save(testUserIvan);

        mockMvc.perform(delete("/users/" + savedUser.getId()))
            .andExpect(status().isOk());

        assertEquals(Optional.empty(), userRepository.findById(savedUser.getId()));
    }

    @Test
    void testDeleteNotExistsUser() throws Exception {
        mockMvc.perform(delete("/users/999"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User with id 999 not found"));
    }

}