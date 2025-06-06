package ru.aston.intensive.service;

import lombok.extern.slf4j.Slf4j;
import ru.aston.intensive.dao.UserDAO;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;

@Slf4j
public class UserService {

    public UserDAO dao;

    public UserService(UserDAO dao) {
        this.dao = dao;
    }

    public User getUserById(Long userId) {
        return dao.findById(userId);
    }

    public Long saveUser(String name, String email) {
        validateData(name, email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return dao.save(user);
    }

    public void updateUser(Long userId, String name, String email) {
        validateData(name, email);

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);

        dao.update(user);
    }

    public void deleteUserById(Long userId) {
        dao.delete(userId);
    }

    private static void validateData(String name, String email) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
            throw new AppException(String.format("Invalid user name=%s, email=%s", name, email));
        }
    }

}
