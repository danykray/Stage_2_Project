package com.project.userService.repository;

import com.project.userService.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_ShouldSaveUser() {
        UserEntity user = new UserEntity();
        user.setName("Иван Петров");
        user.setEmail("ivan@test.com");
        user.setAge(25);

        UserEntity savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Иван Петров");
        assertThat(savedUser.getEmail()).isEqualTo("ivan@test.com");
        assertThat(savedUser.getAge()).isEqualTo(25);
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        UserEntity user = new UserEntity();
        user.setName("Мария Иванова");
        user.setEmail("maria@test.com");
        user.setAge(30);
        UserEntity savedUser = userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Мария Иванова");
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        UserEntity user = new UserEntity();
        user.setName("Петр Сидоров");
        user.setEmail("petr@test.com");
        user.setAge(35);
        userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findByEmail("petr@test.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Петр Сидоров");
    }

    @Test
    void findByAgeGreaterThan_ShouldReturnUsersOlderThanAge() {
        userRepository.save(createUser("Молодой", "young@test.com", 18));
        userRepository.save(createUser("Средний", "middle@test.com", 25));
        userRepository.save(createUser("Взрослый", "adult@test.com", 35));

        List<UserEntity> users = userRepository.findByAgeGreaterThan(20);

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getName)
                .containsExactlyInAnyOrder("Средний", "Взрослый");
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        userRepository.save(createUser("Тест", "test@test.com", 20));

        boolean exists = userRepository.existsByEmail("test@test.com");

        assertThat(exists).isTrue();
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        UserEntity savedUser = userRepository.save(createUser("Для удаления", "delete@test.com", 25));

        userRepository.deleteById(savedUser.getId());

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());
        assertThat(foundUser).isEmpty();
    }

    private UserEntity createUser(String name, String email, int age) {
        UserEntity user = new UserEntity();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }
}