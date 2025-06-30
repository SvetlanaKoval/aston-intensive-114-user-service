package ru.aston.intensive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.UserNotFoundException;
import ru.aston.intensive.exception.ValidateException;
import ru.aston.intensive.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
    }

    public User saveUser(String name, String email) {
        validateData(name, email);

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return repository.save(user);
    }

    public void updateUser(Long userId, String name, String email) {
        validateData(name, email);

        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        user.setName(name);
        user.setEmail(email);
        repository.save(user);
    }

    public void deleteUserById(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(String.format("User with id %s not found", userId)));
        repository.delete(user);
    }

    private void validateData(String name, String email) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || email.isBlank()) {
            throw new ValidateException(String.format("Invalid user name=%s, email=%s", name, email));
        }
    }

}
