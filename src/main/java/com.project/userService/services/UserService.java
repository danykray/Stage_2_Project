package com.project.userService.services;

import com.project.userService.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String name, String email, int age);

    Optional<User> findUserById(Long id);

    List<User> findAllUsers();

    User updateUser(Long id, String name, String email, Integer age);

    void deleteUser(Long id);

    Optional<User> findUserByEmail(String email);

    List<User> findUsersOlderThan(int age);

    boolean isUserExists(Long id);

    long getUsersCount();
}
