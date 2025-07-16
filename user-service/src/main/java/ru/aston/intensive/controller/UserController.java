package ru.aston.intensive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.intensive.dto.UserDto;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.UserServiceExceptionHandler;
import ru.aston.intensive.mapper.UserMapper;
import ru.aston.intensive.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User management", description = "API для управления пользователями")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserServiceExceptionHandler.class);

    private final UserService userService;
    private final UserMapper mapper;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public List<EntityModel<UserDto>> getAllUsers() {
        return userService.getAllUsers().stream()
            .map(this::userToEntityModel)
            .collect(Collectors.toList());
    }

    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{userId}")
    public EntityModel<UserDto> getUserById(@Parameter(description = "Id пользователя", example = "1")
                                            @PathVariable Long userId) {
        log.debug("Going to controller: find user with id - {}", userId);
        User userById = userService.getUserById(userId);

        return userToEntityModel(userById);

    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> saveNewUser(@Parameter(description = "Имя пользователя", example = "Ivan Ivanov")
                                            @RequestParam("name") String name,
                                            @Parameter(description = "Email пользователя", example = "ivan@example.com")
                                            @RequestParam("email") String email) {
        log.debug("Going to controller: save new user with name - {}, email - {}", name, email);
        User user = userService.saveUser(name, email);

        return userToEntityModel(user);
    }

    @Operation(summary = "Обновить данные пользователя")
    @PostMapping("/{userId}")
    public void updateUser(@Parameter(description = "Id пользователя", example = "1")
                           @PathVariable Long userId,
                           @Parameter(description = "Новое имя пользователя", example = "Ivan Ivanov")
                           @RequestParam("name") String name,
                           @Parameter(description = "Новый email пользователя", example = "ivan@example.com")
                           @RequestParam("email") String email) {
        log.debug("Going to controller: update user with id - {}, set name - {}, set email - {}", userId, name, email);
        userService.updateUser(userId, name, email);
    }

    @Operation(summary = "Удалить пользователя по id")
    @DeleteMapping("/{userId}")
    public void deleteUser(@Parameter(description = "Id пользователя", example = "1")
                           @PathVariable Long userId) {
        log.debug("Going to controller: delete user with id - {}", userId);
        userService.deleteUserById(userId);
    }

    private EntityModel<UserDto> userToEntityModel(User user) {
        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel();
        Link getAllLink = linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users");
        Link saveLink = linkTo(methodOn(UserController.class).saveNewUser(null, null)).withRel("add-user");
        Link updateLink = Link.of(linkTo(UserController.class).slash(user.getId()).toString(), "update-user");
        Link deleteLink = Link.of(linkTo(UserController.class).slash(user.getId()).toString(), "delete-user");

        UserDto userDto = mapper.mapUserToUserDto(user);

        return EntityModel.of(userDto, selfLink, getAllLink, saveLink, updateLink, deleteLink);
    }

}
