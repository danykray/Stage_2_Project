package com.project.userService.dao;

import com.project.userService.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    User create(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    User update(User user);

    void delete(Long id);

    Optional<User> findByEmail(String email);

    List<User> findByAgeGreaterThan(int age);
}