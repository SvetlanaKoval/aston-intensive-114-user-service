package ru.aston.intensive.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.UserNotFoundException;
import ru.aston.intensive.exception.ValidateException;
import ru.aston.intensive.repository.UserRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

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
        verify(repository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(testUserIvan.getName(), savedUser.getName());
        assertEquals(testUserIvan.getEmail(), savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    public void testSaveNewUserAndGetIdNull() {
        userService.saveUser(testUserIvan.getName(), testUserIvan.getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNull(savedUser.getId());
        assertNotNull(savedUser);
    }

    @Test
    public void testSaveNewUserWithInvalidData() {
        ValidateException exception = assertThrows(ValidateException.class, () -> userService.saveUser(null, testUserIvan.getEmail()));
        assertEquals("Invalid user name=null, email=ivan@test.ru", exception.getMessage());

        verify(repository, never()).save(testUserIvan);
    }

    @Test
    public void testGetUserById() {
        Long testUserId = testUserIvan.getId();
        when(repository.findById(testUserId)).thenReturn(Optional.of(testUserIvan));

        User returnedUser = userService.getUserById(testUserId);

        assertEquals(testUserIvan.getId(), returnedUser.getId());
        assertEquals(testUserIvan.getName(), returnedUser.getName());
        assertEquals(testUserIvan.getEmail(), returnedUser.getEmail());

        verify(repository).findById(testUserId);
    }

    @Test
    public void testGetNotExistsUserById() {
        Long testUserId = 999L;
        when(repository.findById(testUserId)).thenThrow(new UserNotFoundException("User with id 999 not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(testUserId));
        assertEquals("User with id 999 not found", exception.getMessage());

        verify(repository).findById(testUserId);
    }

    @Test
    public void testUpdateUser() {
        Long testUserId = testUserIvan.getId();
        when(repository.findById(testUserId)).thenReturn(Optional.of(testUserIvan));

        userService.updateUser(testUserId, testUserIvan.getName(), testUserIvan.getEmail());
        verify(repository).findById(testUserId);

        User updatedUser = userService.getUserById(testUserId);
        assertNotNull(updatedUser.getId());
        assertEquals(testUserId, updatedUser.getId());
        assertEquals(testUserIvan.getName(), updatedUser.getName());
        assertEquals(testUserIvan.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserWithInvalidData() {
        ValidateException exception =
            assertThrows(ValidateException.class, () -> userService.updateUser(testUserIvan.getId(), null, testUserIvan.getEmail()));
        assertEquals("Invalid user name=null, email=ivan@test.ru", exception.getMessage());

        verify(repository, never()).findById(testUserIvan.getId());
    }

    @Test
    public void testDeleteUserById() {
        Long testUserId = testUserIvan.getId();
        when(repository.findById(testUserId)).thenReturn(Optional.of(testUserIvan));

        userService.deleteUserById(testUserId);
        verify(repository).findById(testUserId);

        when(repository.findById(testUserId)).thenThrow(new UserNotFoundException("User with id 999 not found"));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(testUserId));
        assertEquals("User with id 999 not found", exception.getMessage());
    }

}