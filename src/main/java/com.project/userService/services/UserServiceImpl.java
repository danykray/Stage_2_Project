package com.project.userService.services;

import com.project.userService.dao.UserDAO;
import com.project.userService.dao.UserDAOImpl;
import com.project.userService.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Objects;
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
    public UserEntity createUser(String name, String email, int age) {
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

        Optional<UserEntity> existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        UserEntity user = new UserEntity(name.trim(), email.trim(), age);
        return userDAO.create(user);
    }

    @Override
    public Optional<UserEntity> findUserById(Long id) {
        logger.info("Finding user by id: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDAO.findById(id);
    }

    @Override
    public List<UserEntity> findAllUsers() {
        logger.info("Finding all users");
        return userDAO.findAll();
    }

    @Override
    public UserEntity updateUser(Long id, String name, String email, Integer age) {
        logger.info("Updating user with id: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        UserEntity user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " not found"));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }

        if (email != null && !email.trim().isEmpty()) {
            Optional<UserEntity> existingUser = userDAO.findByEmail(email.trim());
            if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), id)) {
                throw new IllegalArgumentException("Email " + email + " already in use by another user");
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

        Optional<UserEntity> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }

        userDAO.delete(id);
    }

    @Override
    public Optional<UserEntity> findUserByEmail(String email) {
        logger.info("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        return userDAO.findByEmail(email.trim());
    }

    @Override
    public List<UserEntity> findUsersOlderThan(int age) {
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
