package com.project.userService.repository;

import com.project.userService.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link UserEntity}.
 * <p>
 * Предоставляет методы для доступа к данным пользователей в базе данных.
 * Расширяет {@link JpaRepository}, что дает готовые CRUD-методы:
 * {@code save()}, {@code findById()}, {@code findAll()}, {@code deleteById()} и другие.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Находит пользователя по email адресу.
     * <p>
     * Email является уникальным полем, поэтому метод возвращает
     * не более одного пользователя. Поиск регистрозависимый.
     * </p>
     *
     * @param email email адрес пользователя (не может быть null)
     * @return {@link Optional}, содержащий найденного пользователя,
     *         или пустой Optional, если пользователь не найден
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Находит всех пользователей, возраст которых превышает указанное значение.
     * <p>
     * Метод возвращает список пользователей, у которых возраст строго больше
     * переданного значения. Результат не сортируется по умолчанию,
     * при необходимости сортировку можно добавить через параметры метода.
     * </p>
     *
     * @param age минимальный возраст (должен быть положительным числом)
     * @return список пользователей старше указанного возраста.
     *         Если подходящих пользователей нет, возвращает пустой список
     */
    List<UserEntity> findByAgeGreaterThan(int age);

    /**
     * Проверяет существование пользователя с указанным email адресом.
     * <p>
     * Метод полезен для проверки уникальности email перед созданием
     * или обновлением пользователя.
     * </p>
     *
     * @param email email адрес для проверки (не может быть null)
     * @return {@code true}, если пользователь с таким email существует,
     *         {@code false} в противном случае
     */
    boolean existsByEmail(String email);
}
