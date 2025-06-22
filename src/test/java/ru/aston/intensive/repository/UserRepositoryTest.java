package ru.aston.intensive.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import ru.aston.intensive.AbstractIntegrationTest;
import ru.aston.intensive.entity.User;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserRepositoryTest extends AbstractIntegrationTest {

    private User testUserAnna;

    @BeforeEach
    void setUp() {
        testUserAnna = new User();
        testUserAnna.setName("Anna");
        testUserAnna.setEmail("anna@test.ru");
    }

    @Test
    public void testFindAll() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@test.ru");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@test.ru");

        User user3 = new User();
        user3.setName("user3");
        user3.setEmail("user3@test.ru");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        assertThat(userRepository.findAll())
            .hasSize(3)
            .extracting(User::getName)
            .containsExactly("user1", "user2", "user3");
    }

    @Test
    public void testSaveNewUserAndReturn() {
        User savedUser = userRepository.save(testUserAnna);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    public void testSaveAndCheckPersist() {
        userRepository.save(testUserAnna);

        assertThat(testUserAnna.getId()).isGreaterThan(0);
    }

    @Test
    public void testSaveAndGetUser() {
        User savedUser = userRepository.save(testUserAnna);
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

        assertThat(retrievedUser)
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user.getId()).isEqualTo(savedUser.getId());
                assertThat(user.getName()).isEqualTo(testUserAnna.getName());
                assertThat(user.getEmail()).isEqualTo(testUserAnna.getEmail());
            });
    }

    @Test
    public void testSaveNewUserWithNullNameAndEmail() {
        User user = new User();

        assertThatThrownBy(() -> userRepository.save(user))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void testFindById() {
        userRepository.save(testUserAnna);
        Optional<User> retrievedUser = userRepository.findById(testUserAnna.getId());

        assertThat(retrievedUser)
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user.getName()).isEqualTo(testUserAnna.getName());
                assertThat(user.getEmail()).isEqualTo(testUserAnna.getEmail());
            });
    }

    @Test
    public void testFindByIdNotExistsUser() {
        Optional<User> notFoundedUser = userRepository.findById(5L);

        assertThat(notFoundedUser).isEmpty();
    }

    @Test
    public void testUpdateUser() {
        userRepository.save(testUserAnna);
        testUserAnna.setName("Inna");
        testUserAnna.setEmail("inna@test.ru");
        userRepository.save(testUserAnna);

        Optional<User> updatedUser = userRepository.findById(testUserAnna.getId());

        assertThat(updatedUser)
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user.getName()).isEqualTo(testUserAnna.getName());
                assertThat(user.getEmail()).isEqualTo(testUserAnna.getEmail());
            });
    }

    @Test
    public void testDeleteUser() {
        User testUser = userRepository.save(testUserAnna);
        assertThat(userRepository.findById(testUser.getId())).isPresent();

        userRepository.delete(testUser);
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }

}