package ru.aston.intensive.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.intensive.dto.UserDto;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.UserServiceExceptionHandler;
import ru.aston.intensive.mapper.UserMapper;
import ru.aston.intensive.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserServiceExceptionHandler.class);

    private final UserService userService;
    private final UserMapper mapper;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.debug("Going to controller: find user with id - {}", userId);
        return mapper.mapUserToUserDto(userService.getUserById(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveNewUser(@RequestParam("name") String name,
                               @RequestParam("email") String email) {
        log.debug("Going to controller: save new user with name - {}, email - {}", name, email);
        User user = userService.saveUser(name, email);
        return mapper.mapUserToUserDto(user);
    }

    @PostMapping("/{userId}")
    public void updateUser(@PathVariable Long userId,
                           @RequestParam("name") String name,
                           @RequestParam("email") String email) {
        log.debug("Going to controller: update user with id - {}, set name - {}, set email - {}", userId, name, email);
        userService.updateUser(userId, name, email);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("Going to controller: delete user with id - {}", userId);
        userService.deleteUserById(userId);
    }

}
