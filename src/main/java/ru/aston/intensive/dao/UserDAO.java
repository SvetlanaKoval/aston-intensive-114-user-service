package ru.aston.intensive.dao;

import ru.aston.intensive.entity.User;

public interface UserDAO {

    Long save(User user);

    User findById(Long id);

    void update(User user);

    void delete(Long id);

}
