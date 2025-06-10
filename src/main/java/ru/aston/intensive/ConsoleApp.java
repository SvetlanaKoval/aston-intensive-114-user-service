package ru.aston.intensive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.intensive.dao.UserDAOHibernateImpl;
import ru.aston.intensive.entity.User;
import ru.aston.intensive.exception.AppException;
import ru.aston.intensive.service.UserService;
import ru.aston.intensive.utils.HibernateUtil;
import java.util.Scanner;

public class ConsoleApp {

    private static final Logger log = LoggerFactory.getLogger(ConsoleApp.class);
    static UserService userService = new UserService(new UserDAOHibernateImpl(HibernateUtil.getSessionFactory()));
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            try {
                runner();
            } catch (AppException exc) {
                log.error(exc.getMessage());
            }
        }
    }

    private static void runner() {
        System.out.println(showMenu());
        String choice = scanner.nextLine();

        switch (Integer.parseInt(choice)) {
            case 1:
                createUser();
                System.out.println(System.lineSeparator());
                break;
            case 2:
                User user = getUser();
                System.out.println("Найден пользователь: " + user);
                System.out.println(System.lineSeparator());
                break;
            case 3:
                updateUser();
                System.out.println(System.lineSeparator());
                break;
            case 4:
                deleteUser();
                System.out.println(System.lineSeparator());
                break;
            case 5:
                System.exit(0);
            default:
                log.warn("Unknown menu id: {}", choice);
                System.out.println("Номер операции не идентифицируется");
        }
    }

    public static String showMenu() {
        return "Введите номер операции\n" +
            "1. Добавить нового пользователя\n" +
            "2. Получить данные о пользователе\n" +
            "3. Обновить данные пользователя\n" +
            "4. Удалить пользователя\n" +
            "5. Выйти\n";
    }

    public static void createUser() {
        log.info("Creating new user");

        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите почту: ");
        String email = scanner.nextLine();

        userService.saveUser(name, email);
    }

    public static User getUser() {
        log.info("Getting existing user");

        System.out.print("Введите id пользователя: ");

        Long userId = getUserId(scanner.nextLine());
        return userService.getUserById(userId);
    }

    public static void updateUser() {
        log.info("Updating existing user");

        System.out.print("Введите id пользователя: ");
        Long userId = getUserId(scanner.nextLine());

        System.out.print("Введите новое имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите новую почту: ");
        String email = scanner.nextLine();

        userService.updateUser(userId, name, email);
    }

    public static void deleteUser() {
        log.info("Deleting existing user");

        System.out.print("Введите id пользователя: ");
        Long userId = getUserId(scanner.nextLine());

        userService.deleteUserById(userId);
    }

    private static Long getUserId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new AppException(String.format("Invalid id format: %s", id));
        }
    }

}
