package com.project.userService.service;

import com.project.userService.dto.UserRequestDto;
import com.project.userService.dto.UserResponseDto;
import com.project.userService.entity.UserEntity;
import com.project.userService.mapper.UserMapper;
import com.project.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        logger.info("Creating new user with name: {}, email: {}, age: {}",
                requestDto.getName(), requestDto.getEmail(), requestDto.getAge());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + requestDto.getEmail() + " already exists");
        }

        UserEntity entity = userMapper.toEntity(requestDto);
        UserEntity savedEntity = userRepository.save(entity);
        logger.info("User created successfully with id: {}", savedEntity.getId());

        return userMapper.toResponseDto(savedEntity);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        logger.info("Finding user by id: {}", id);

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        return userMapper.toResponseDto(entity);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        logger.info("Finding all users");

        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto requestDto) {
        logger.info("Updating user with id: {}", id);

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        if (!entity.getEmail().equals(requestDto.getEmail())
                && userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Email " + requestDto.getEmail() + " already in use by another user");
        }

        entity.setName(requestDto.getName());
        entity.setEmail(requestDto.getEmail());
        entity.setAge(requestDto.getAge());

        UserEntity updatedEntity = userRepository.save(entity);
        logger.info("User updated successfully with id: {}", id);

        return userMapper.toResponseDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully with id: {}", id);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        logger.info("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        UserEntity entity = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));

        return userMapper.toResponseDto(entity);
    }

    @Override
    public List<UserResponseDto> getUsersOlderThan(int age) {
        logger.info("Finding users older than: {}", age);

        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }

        return userRepository.findByAgeGreaterThan(age).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserExists(Long id) {
        logger.debug("Checking if user exists with id: {}", id);
        return userRepository.existsById(id);
    }

    @Override
    public long getUsersCount() {
        logger.debug("Getting total users count");
        return userRepository.count();
    }
}
