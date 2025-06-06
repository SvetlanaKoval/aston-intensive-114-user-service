package ru.aston.intensive.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.intensive.dao.UserDAO;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User testUserIvan;

    @BeforeEach
    void createTestUser() {
        testUserIvan = new User();
        testUserIvan.setId(1L);
        testUserIvan.setName("Ivan");
        testUserIvan.setEmail("ivan@test.ru");
    }

    @Test
    public void testSaveNewUser() {
        userService.saveUser(testUserIvan.getName(), testUserIvan.getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(testUserIvan.getName(), savedUser.getName());
        assertEquals(testUserIvan.getEmail(), savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    public void testSaveNewUserAndGetIdNull() {
        userService.saveUser(testUserIvan.getName(), testUserIvan.getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNull(savedUser.getId());
        assertNotNull(savedUser);
    }

    @Test
    public void testSaveNewUserWithInvalidData() {
        assertThrows(AppException.class, () -> userService.saveUser(null, testUserIvan.getEmail()));

        verify(userDAO, times(0)).save(testUserIvan);
    }

    @Test
    public void testGetUserById() {
        Long testUserId = testUserIvan.getId();
        when(userDAO.findById(testUserId)).thenReturn(testUserIvan);

        User returnedUser = userService.getUserById(testUserId);

        assertEquals(testUserIvan.getId(), returnedUser.getId());
        assertEquals(testUserIvan.getName(), returnedUser.getName());
        assertEquals(testUserIvan.getEmail(), returnedUser.getEmail());

        verify(userDAO, times(1)).findById(testUserId);
    }

    @Test
    public void testGetNotExistsUserById() {
        Long testUserId = testUserIvan.getId();
        when(userDAO.findById(testUserId)).thenReturn(null);

        User returnedUser = userService.getUserById(testUserId);

        assertNull(returnedUser);

        verify(userDAO, times(1)).findById(testUserId);
    }

    @Test
    public void testUpdateUser() {
        Long testUserId = testUserIvan.getId();
        userService.updateUser(testUserId, testUserIvan.getName(), testUserIvan.getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();

        assertNotNull(updatedUser.getId());
        assertEquals(testUserId, updatedUser.getId());
        assertEquals(testUserIvan.getName(), updatedUser.getName());
        assertEquals(testUserIvan.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserWithInvalidData() {
        assertThrows(AppException.class, () -> userService.updateUser(testUserIvan.getId(), null, testUserIvan.getEmail()));

        verify(userDAO, times(0)).update(testUserIvan);
    }

    @Test
    public void testDeleteUserById() {
        Long testUserId = testUserIvan.getId();

        userService.deleteUserById(testUserId);

        verify(userDAO, times(1)).delete(testUserId);
    }

}