package com.project.userService.services;

import com.project.userService.dao.UserDAO;
import com.project.userService.dao.UserDAOImpl;
import com.project.userService.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User createUser(String name, String email, int age) {
        logger.info("Creating new user with name: {}, email: {}, age: {}", name, email, age);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }

        Optional<User> existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = new User(name.trim(), email.trim(), age);
        return userDAO.create(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        logger.info("Finding user by id: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        logger.info("Finding all users");
        return userDAO.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email, Integer age) {
        logger.info("Updating user with id: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }

        User user = userOpt.get();

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }

        if (email != null && !email.trim().isEmpty()) {
            Optional<User> existingUser = userDAO.findByEmail(email.trim());
            if (existingUser.isPresent()) {
                existingUser.get();
            }
            user.setEmail(email.trim());
        }

        if (age != null) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Age must be between 0 and 150");
            }
            user.setAge(age);
        }

        return userDAO.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }

        userDAO.delete(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        logger.info("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        return userDAO.findByEmail(email.trim());
    }

    @Override
    public List<User> findUsersOlderThan(int age) {
        logger.info("Finding users older than: {}", age);

        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }

        return userDAO.findByAgeGreaterThan(age);
    }

    @Override
    public boolean isUserExists(Long id) {
        return findUserById(id).isPresent();
    }

    @Override
    public long getUsersCount() {
        return userDAO.findAll().size();
    }
}
