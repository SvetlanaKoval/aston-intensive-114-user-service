package ru.aston.intensive.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDAOHibernateImplTest {

    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14.1-alpine");

    private static SessionFactory testSessionFactory;
    private static UserDAOHibernateImpl userDao;
    private User testUserAnna;

    @BeforeAll
    static void beforeAll() {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .applySetting("hibernate.connection.url", container.getJdbcUrl())
                .applySetting("hibernate.connection.username", container.getUsername())
                .applySetting("hibernate.connection.password", container.getPassword())
                .build();

            Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .getMetadataBuilder()
                .build();

            testSessionFactory = metadata.getSessionFactoryBuilder().build();
            userDao = new UserDAOHibernateImpl(testSessionFactory);
        } catch (Exception ex) {
            throw new AppException("Initial TestSessionFactory creation failed.", ex);
        }
    }

    @AfterAll
    static void closeSessionFactory() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
    }

    @BeforeEach
    void createTestUser() {
        testUserAnna = new User();
        testUserAnna.setName("Anna");
        testUserAnna.setEmail("anna@test.ru");
    }

    @AfterEach
    void clearTableUser() {
        try (Session session = testSessionFactory.openSession()) {
            session.beginTransaction();

            session.createMutationQuery("DELETE from User").executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    public void testSaveNewUserAndReturnId() {
        Long userId = userDao.save(testUserAnna);

        assertNotNull(userId);
        assertTrue(userId > 0);
    }

    @Test
    public void testSaveAndCheckPersist() {
        userDao.save(testUserAnna);

        assertTrue(testUserAnna.getId() > 0);
    }

    @Test
    public void testSaveAndGetUser() {
        long userId = userDao.save(testUserAnna);
        User retrievedUser = userDao.findById(userId);

        assertNotNull(retrievedUser.getId());
        assertEquals(userId, retrievedUser.getId());
        assertEquals(testUserAnna.getName(), retrievedUser.getName());
        assertEquals(testUserAnna.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testSaveNewUserWithNullNameAndEmail() {
        User user = new User();

        assertThrows(IllegalStateException.class, () -> userDao.save(user));
    }

    @Test
    public void testFindById() {
        userDao.save(testUserAnna);
        Long id = testUserAnna.getId();

        User retrievedUser = userDao.findById(id);

        assertNotNull(retrievedUser);
        assertEquals(testUserAnna.getName(), retrievedUser.getName());
        assertEquals(testUserAnna.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testFindByIdNotExistsUser() {

        assertThrows(AppException.class, () -> userDao.findById(5L), "User with id=5 not found");
    }

    @Test
    public void testUpdateUser() {
        userDao.save(testUserAnna);
        testUserAnna.setName("Inna");
        testUserAnna.setEmail("inna@test.ru");

        userDao.update(testUserAnna);
        User updatedUser = userDao.findById(testUserAnna.getId());

        assertEquals(testUserAnna.getName(), updatedUser.getName());
        assertEquals(testUserAnna.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdateNotExistsUser() {
        testUserAnna.setId(5L);

        assertThrows(AppException.class, () -> userDao.update(testUserAnna), "User with id=5 not found");
    }

    @Test
    public void testUpdateDeletedUser() {
        Long testUserId = userDao.save(testUserAnna);
        userDao.delete(testUserId);

        assertThrows(AppException.class, () -> userDao.update(testUserAnna), String.format("User with id=%s not found", testUserId));
    }

    @Test
    public void testUpdateRollbackWhenException() {
        Long testUserId = userDao.save(testUserAnna);
        String testUserEmail = testUserAnna.getEmail();
        User invalidUser = new User();
        invalidUser.setId(testUserId);
        invalidUser.setName("Ivan");

        assertThrows(AppException.class, () -> userDao.update(invalidUser));

        User existingUser = userDao.findById(testUserId);

        assertEquals(testUserEmail, existingUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        Long testUserId = userDao.save(testUserAnna);

        userDao.delete(testUserId);

        assertThrows(AppException.class, () -> userDao.findById(testUserId));
    }

    @Test
    public void testDeleteNotExistsUser() {
        Long testUserId = 5L;
        testUserAnna.setId(testUserId);

        assertThrows(AppException.class, () -> userDao.delete(testUserId), String.format("User with id=%s not found", testUserId));
    }

}