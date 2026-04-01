package com.project;

import com.project.userService.entity.User;
import com.project.userService.services.UserService;
import com.project.userService.services.UserServiceImpl;
import com.project.userService.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final UserService userService = new UserServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Запуск приложения User Service");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Выберите опцию: ");

            try {
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        findUserById();
                        break;
                    case 3:
                        findAllUsers();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
                        break;
                    case 6:
                        findUserByEmail();
                        break;
                    case 7:
                        findUsersByAge();
                        break;
                    case 8:
                        running = false;
                        logger.info("Завершение работы приложения");
                        break;
                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
                logger.error("Ошибка в главном цикле: ", e);
            }
        }

        scanner.close();
        HibernateUtil.shutdown();
    }

    private static void printMenu() {
        System.out.println("\n=== Меню User Service ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Найти пользователя по email");
        System.out.println("7. Найти пользователей старше указанного возраста");
        System.out.println("8. Выход");
    }

    private static void createUser() {
        System.out.println("\n=== Создание нового пользователя ===");

        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        try {
            User user = userService.createUser(name, email, age);
            System.out.println("Пользователь успешно создан: " + user);
        } catch (IllegalArgumentException e) {
            System.err.println("Не удалось создать пользователя: " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.println("\n=== Поиск пользователя по ID ===");
        Long id = getLongInput("Введите ID пользователя: ");

        Optional<User> userOpt = userService.findUserById(id);

        if (userOpt.isPresent()) {
            System.out.println("Пользователь найден: " + userOpt.get());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }

    private static void findAllUsers() {
        System.out.println("\n=== Все пользователи ===");
        List<User> users = userService.findAllUsers();

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
        } else {
            System.out.println("Всего пользователей: " + users.size());
            users.forEach(System.out::println);
        }
    }

    private static void updateUser() {
        System.out.println("\n=== Обновление пользователя ===");
        Long id = getLongInput("Введите ID пользователя для обновления: ");

        Optional<User> userOpt = userService.findUserById(id);

        if (userOpt.isPresent()) {
            User currentUser = userOpt.get();
            System.out.println("Текущий пользователь: " + currentUser);

            System.out.print("Введите новое имя (оставьте пустым, чтобы не менять): ");
            String name = scanner.nextLine();
            if (name.trim().isEmpty()) {
                name = null;
            }

            System.out.print("Введите новый email (оставьте пустым, чтобы не менять): ");
            String email = scanner.nextLine();
            if (email.trim().isEmpty()) {
                email = null;
            }

            System.out.print("Введите новый возраст (оставьте пустым, чтобы не менять): ");
            String ageStr = scanner.nextLine();
            Integer age = null;
            if (!ageStr.trim().isEmpty()) {
                age = Integer.parseInt(ageStr);
            }

            try {
                User updatedUser = userService.updateUser(id, name, email, age);
                System.out.println("Пользователь успешно обновлен: " + updatedUser);
            } catch (IllegalArgumentException e) {
                System.err.println("Не удалось обновить пользователя: " + e.getMessage());
            }
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }

    private static void deleteUser() {
        System.out.println("\n=== Удаление пользователя ===");
        Long id = getLongInput("Введите ID пользователя для удаления: ");

        Optional<User> userOpt = userService.findUserById(id);

        if (userOpt.isPresent()) {
            System.out.println("Пользователь для удаления: " + userOpt.get());
            System.out.print("Вы уверены? (y/n): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("y")) {
                try {
                    userService.deleteUser(id);
                    System.out.println("Пользователь с ID " + id + " успешно удален");
                } catch (IllegalArgumentException e) {
                    System.err.println("Не удалось удалить пользователя: " + e.getMessage());
                }
            } else {
                System.out.println("Удаление отменено");
            }
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }

    private static void findUserByEmail() {
        System.out.println("\n=== Поиск пользователя по email ===");
        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        try {
            Optional<User> userOpt = userService.findUserByEmail(email);

            if (userOpt.isPresent()) {
                System.out.println("Пользователь найден: " + userOpt.get());
            } else {
                System.out.println("Пользователь с email " + email + " не найден");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void findUsersByAge() {
        System.out.println("\n=== Поиск пользователей по возрасту ===");
        int age = getIntInput("Введите минимальный возраст: ");

        try {
            List<User> users = userService.findUsersOlderThan(age);

            if (users.isEmpty()) {
                System.out.println("Пользователи старше " + age + " лет не найдены");
            } else {
                System.out.println("Найдено " + users.size() + " пользователей старше " + age + " лет:");
                users.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Пожалуйста, введите число: ");
            scanner.next();
        }
        int result = scanner.nextInt();
        scanner.nextLine();
        return result;
    }

    private static Long getLongInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextLong()) {
            System.out.print("Пожалуйста, введите число: ");
            scanner.next();
        }
        Long result = scanner.nextLong();
        scanner.nextLine();
        return result;
    }
}