package com.project.userService.dao;

import com.project.userService.entity.UserEntity;
import com.project.userService.util.TestHibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOIntegrationTest {

    private UserDAO userDAO;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUpAll() {
        sessionFactory = TestHibernateUtil.getSessionFactory();
        userDAO = new TestUserDAOImpl(sessionFactory);
    }

    @BeforeEach
    void setUp() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM UserEntity").executeUpdate();
            transaction.commit();
        }
    }

    @AfterAll
    void tearDownAll() {
        TestHibernateUtil.shutdown();
    }

    @Test
    void createShouldSaveUserWhenUserIsValid() {
        UserEntity user = new UserEntity("Иван Петров", "ivan@test.com", 25);

        UserEntity savedUser = userDAO.create(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Иван Петров");
        assertThat(savedUser.getEmail()).isEqualTo("ivan@test.com");
        assertThat(savedUser.getAge()).isEqualTo(25);
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    void findByIdShouldReturnUserWhenUserExists() {
        UserEntity user = new UserEntity("Мария Иванова", "maria@test.com", 30);
        UserEntity savedUser = userDAO.create(user);

        Optional<UserEntity> foundUser = userDAO.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Мария Иванова");
        assertThat(foundUser.get().getEmail()).isEqualTo("maria@test.com");
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<UserEntity> foundUser = userDAO.findById(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void findAllShouldReturnAllUsers() {
        userDAO.create(new UserEntity("Петр Сидоров", "petr@test.com", 28));
        userDAO.create(new UserEntity("Анна Козлова", "anna@test.com", 35));
        userDAO.create(new UserEntity("Сергей Смирнов", "sergey@test.com", 22));

        List<UserEntity> users = userDAO.findAll();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(UserEntity::getName)
                .containsExactlyInAnyOrder("Петр Сидоров", "Анна Козлова", "Сергей Смирнов");
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoUsers() {
        List<UserEntity> users = userDAO.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    void updateShouldUpdateUserWhenUserExists() {
        UserEntity user = new UserEntity("Дмитрий Орлов", "dmitry@test.com", 40);
        UserEntity savedUser = userDAO.create(user);

        savedUser.setName("Дмитрий Орлов Обновленный");
        savedUser.setAge(41);
        UserEntity updatedUser = userDAO.update(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Дмитрий Орлов Обновленный");
        assertThat(updatedUser.getAge()).isEqualTo(41);

        Optional<UserEntity> foundUser = userDAO.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Дмитрий Орлов Обновленный");
    }

    @Test
    void deleteShouldRemoveUserWhenUserExists() {
        UserEntity user = new UserEntity("Елена Васнецова", "elena@test.com", 27);
        UserEntity savedUser = userDAO.create(user);

        userDAO.delete(savedUser.getId());

        Optional<UserEntity> foundUser = userDAO.findById(savedUser.getId());
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByEmailShouldReturnUserWhenEmailExists() {
        UserEntity user = new UserEntity("Ольга Новикова", "olga@test.com", 33);
        userDAO.create(user);

        Optional<UserEntity> foundUser = userDAO.findByEmail("olga@test.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Ольга Новикова");
    }

    @Test
    void findByEmailShouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<UserEntity> foundUser = userDAO.findByEmail("nonexistent@test.com");

        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByAgeGreaterThanShouldReturnUsersWithAgeGreaterThanSpecified() {
        userDAO.create(new UserEntity("Молодой", "young@test.com", 18));
        userDAO.create(new UserEntity("Средний", "middle@test.com", 25));
        userDAO.create(new UserEntity("Взрослый", "adult@test.com", 35));
        userDAO.create(new UserEntity("Пожилой", "senior@test.com", 50));

        List<UserEntity> users = userDAO.findByAgeGreaterThan(30);

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getName)
                .containsExactlyInAnyOrder("Взрослый", "Пожилой");
    }

    @Test
    void findByAgeGreaterThanShouldReturnEmptyListWhenNoUsers() {
        userDAO.create(new UserEntity("Молодой", "young@test.com", 18));
        userDAO.create(new UserEntity("Средний", "middle@test.com", 25));

        List<UserEntity> users = userDAO.findByAgeGreaterThan(100);

        assertThat(users).isEmpty();
    }

    @Test
    void createShouldThrowExceptionWhenDuplicateEmail() {
        UserEntity user1 = new UserEntity("Первый", "duplicate@test.com", 20);
        userDAO.create(user1);

        UserEntity user2 = new UserEntity("Второй", "duplicate@test.com", 25);

        assertThatThrownBy(() -> userDAO.create(user2))
                .isInstanceOf(RuntimeException.class);
    }
}