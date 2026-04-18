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

/**
 * Реализация сервисного слоя для управления пользователями.
 * <p>
 * Предоставляет реализацию методов бизнес-логики с использованием Spring Data JPA.
 * Использует {@link UserRepository} для доступа к данным и {@link UserMapper}
 * для преобразования между Entity и DTO.
 * </p>
 * <p>
 * Все методы, изменяющие данные, аннотированы {@link Transactional} для обеспечения
 * атомарности операций.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Создает нового пользователя.
     * <p>
     * Выполняет проверку на уникальность email перед сохранением.
     * При успешной валидации преобразует DTO в Entity, сохраняет в базу данных
     * и возвращает DTO сохраненного пользователя.
     * </p>
     *
     * @param requestDto DTO с данными для создания пользователя
     * @return DTO созданного пользователя с заполненным ID и createdAt
     * @throws IllegalArgumentException если пользователь с таким email уже существует
     */
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

    /**
     * Находит пользователя по идентификатору.
     * <p>
     * Выполняет поиск в базе данных. Если пользователь не найден, выбрасывает
     * исключение с информативным сообщением.
     * </p>
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя
     * @throws IllegalArgumentException если пользователь с указанным ID не найден
     */
    @Override
    public UserResponseDto getUserById(Long id) {
        logger.info("Finding user by id: {}", id);

        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        return userMapper.toResponseDto(entity);
    }

    /**
     * Возвращает список всех пользователей.
     * <p>
     * Извлекает всех пользователей из базы данных и преобразует их в DTO.
     * </p>
     *
     * @return список DTO всех пользователей (может быть пустым)
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        logger.info("Finding all users");

        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет информацию о существующем пользователе.
     * <p>
     * Проверяет существование пользователя, затем обновляет все поля из DTO.
     * При изменении email выполняет проверку на уникальность.
     * </p>
     *
     * @param id идентификатор обновляемого пользователя
     * @param requestDto DTO с обновленными данными
     * @return DTO обновленного пользователя
     * @throws IllegalArgumentException если:
     *         <ul>
     *           <li>пользователь с указанным ID не найден</li>
     *           <li>новый email уже используется другим пользователем</li>
     *         </ul>
     */
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

    /**
     * Удаляет пользователя по идентификатору.
     * <p>
     * Проверяет существование пользователя перед удалением.
     * </p>
     *
     * @param id идентификатор пользователя для удаления
     * @throws IllegalArgumentException если пользователь с указанным ID не найден
     */
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

    /**
     * Находит пользователя по email адресу.
     * <p>
     * Выполняет поиск пользователя с указанным email. Email является уникальным полем.
     * </p>
     *
     * @param email email пользователя
     * @return DTO найденного пользователя
     * @throws IllegalArgumentException если:
     *         <ul>
     *           <li>email равен null или пустой строке</li>
     *           <li>пользователь с указанным email не найден</li>
     *         </ul>
     */
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

    /**
     * Находит всех пользователей старше указанного возраста.
     * <p>
     * Использует метод репозитория {@link UserRepository#findByAgeGreaterThan(int)}.
     * </p>
     *
     * @param age минимальный возраст
     * @return список DTO пользователей старше указанного возраста
     * @throws IllegalArgumentException если age меньше 0
     */
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

    /**
     * Проверяет существование пользователя с указанным идентификатором.
     *
     * @param id идентификатор пользователя
     * @return true если пользователь существует, false в противном случае
     */
    @Override
    public boolean isUserExists(Long id) {
        logger.debug("Checking if user exists with id: {}", id);
        return userRepository.existsById(id);
    }

    /**
     * Возвращает общее количество пользователей в базе данных.
     *
     * @return количество пользователей
     */
    @Override
    public long getUsersCount() {
        logger.debug("Getting total users count");
        return userRepository.count();
    }
}