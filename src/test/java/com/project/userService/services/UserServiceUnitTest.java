package com.project.userService.services;

import com.project.userService.dao.UserDAO;
import com.project.userService.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity("Тестовый Пользователь", "test@example.com", 30);
        testUserEntity.setId(1L);
    }

    @Test
    void createUserShouldReturnUserWhenDataIsValid() {
        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userDAO.create(any(UserEntity.class))).thenReturn(testUserEntity);

        UserEntity createdUserEntity = userService.createUser("Тестовый Пользователь", "test@example.com", 30);

        assertThat(createdUserEntity).isNotNull();
        assertThat(createdUserEntity.getName()).isEqualTo("Тестовый Пользователь");
        assertThat(createdUserEntity.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUserEntity.getAge()).isEqualTo(30);

        verify(userDAO, times(1)).findByEmail("test@example.com");
        verify(userDAO, times(1)).create(any(UserEntity.class));
    }

    @Test
    void createUserShouldThrowExceptionWhenNameIsEmpty() {
        assertThatThrownBy(() -> userService.createUser("", "test@example.com", 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");

        assertThatThrownBy(() -> userService.createUser("   ", "test@example.com", 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name cannot be empty");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailIsEmpty() {
        assertThatThrownBy(() -> userService.createUser("Тест", "", 30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be empty");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {-5, -1, 151, 200})
    void createUserShouldThrowExceptionWhenAgeIsInvalid(int age) {
        assertThatThrownBy(() -> userService.createUser("Тест", "test@example.com", age))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Age must be between 0 and 150");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    void createUserShouldThrowExceptionWhenEmailAlreadyExists() {
        when(userDAO.findByEmail("existing@example.com")).thenReturn(Optional.of(testUserEntity));

        assertThatThrownBy(() -> userService.createUser("Новый", "existing@example.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email existing@example.com already exists");

        verify(userDAO, never()).create(any(UserEntity.class));
    }

    @Test
    void findUserByIdShouldReturnUserWhenUserExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUserEntity));

        Optional<UserEntity> foundUserEntity = userService.findUserById(1L);

        assertThat(foundUserEntity).isPresent();
        assertThat(foundUserEntity.get().getId()).isEqualTo(1L);

        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    void findUserByIdShouldReturnEmptyWhenUserDoesNotExist() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        Optional<UserEntity> foundUserEntity = userService.findUserById(999L);

        assertThat(foundUserEntity).isEmpty();
    }

    @Test
    void findUserByIdShouldThrowExceptionWhenIdIsInvalid() {
        assertThatThrownBy(() -> userService.findUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid user ID");

        assertThatThrownBy(() -> userService.findUserById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid user ID");

        verify(userDAO, never()).findById(any());
    }

    @Test
    void findAllUsersShouldReturnListOfUsers() {
        List<UserEntity> expectedUserEntities = List.of(
                new UserEntity("Пользователь 1", "user1@test.com", 20),
                new UserEntity("Пользователь 2", "user2@test.com", 25)
        );
        when(userDAO.findAll()).thenReturn(expectedUserEntities);

        List<UserEntity> userEntities = userService.findAllUsers();

        assertThat(userEntities).hasSize(2);
        assertThat(userEntities).isEqualTo(expectedUserEntities);

        verify(userDAO, times(1)).findAll();
    }

    @Test
    void updateUserShouldUpdateUserWhenDataIsValid() {
        UserEntity updatedUserEntity = new UserEntity("Обновленное имя", "updated@example.com", 35);
        updatedUserEntity.setId(1L);

        when(userDAO.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userDAO.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userDAO.update(any(UserEntity.class))).thenReturn(updatedUserEntity);

        UserEntity result = userService.updateUser(1L, "Обновленное имя", "updated@example.com", 35);

        assertThat(result.getName()).isEqualTo("Обновленное имя");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getAge()).isEqualTo(35);

        verify(userDAO, times(1)).findById(1L);
        verify(userDAO, times(1)).update(any(UserEntity.class));
    }

    @Test
    void updateUserShouldThrowExceptionWhenUserNotFound() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, "Имя", "email@test.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with ID 999 not found");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    @Test
    void updateUserShouldThrowExceptionWhenEmailAlreadyExists() {
        UserEntity otherUserEntity = new UserEntity("Другой", "other@example.com", 40);
        otherUserEntity.setId(2L);

        when(userDAO.findById(1L)).thenReturn(Optional.of(testUserEntity));
        when(userDAO.findByEmail("other@example.com")).thenReturn(Optional.of(otherUserEntity));

        assertThatThrownBy(() -> userService.updateUser(1L, null, "other@example.com", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email other@example.com already in use by another user");

        verify(userDAO, never()).update(any(UserEntity.class));
    }

    @Test
    void deleteUserShouldDeleteUserWhenUserExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUserEntity));
        doNothing().when(userDAO).delete(1L);

        userService.deleteUser(1L);

        verify(userDAO, times(1)).findById(1L);
        verify(userDAO, times(1)).delete(1L);
    }

    @Test
    void deleteUserShouldThrowExceptionWhenUserNotFound() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with ID 999 not found");

        verify(userDAO, never()).delete(any());
    }

    @Test
    void findUserByEmailShouldReturnUserWhenEmailExists() {
        when(userDAO.findByEmail("test@example.com")).thenReturn(Optional.of(testUserEntity));

        Optional<UserEntity> foundUserEntity = userService.findUserByEmail("test@example.com");

        assertThat(foundUserEntity).isPresent();

        verify(userDAO, times(1)).findByEmail("test@example.com");
    }

    @Test
    void findUserByEmailShouldThrowExceptionWhenEmailIsEmpty() {
        assertThatThrownBy(() -> userService.findUserByEmail(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be empty");

        assertThatThrownBy(() -> userService.findUserByEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email cannot be empty");

        verify(userDAO, never()).findByEmail(any());
    }

    @Test
    void findUsersOlderThanShouldReturnListOfUsers() {
        List<UserEntity> expectedUserEntities = List.of(
                new UserEntity("Взрослый", "adult@test.com", 35),
                new UserEntity("Пожилой", "senior@test.com", 50)
        );
        when(userDAO.findByAgeGreaterThan(30)).thenReturn(expectedUserEntities);

        List<UserEntity> userEntities = userService.findUsersOlderThan(30);

        assertThat(userEntities).hasSize(2);
        assertThat(userEntities).isEqualTo(expectedUserEntities);

        verify(userDAO, times(1)).findByAgeGreaterThan(30);
    }

    @Test
    void findUsersOlderThanShouldThrowExceptionWhenAgeIsNegative() {
        assertThatThrownBy(() -> userService.findUsersOlderThan(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Age cannot be negative");

        verify(userDAO, never()).findByAgeGreaterThan(anyInt());
    }

    @Test
    void isUserExistsShouldReturnTrueWhenUserExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(testUserEntity));

        boolean exists = userService.isUserExists(1L);

        assertThat(exists).isTrue();

        verify(userDAO, times(1)).findById(1L);
    }

    @Test
    void isUserExistsShouldReturnFalseWhenUserDoesNotExist() {
        when(userDAO.findById(999L)).thenReturn(Optional.empty());

        boolean exists = userService.isUserExists(999L);

        assertThat(exists).isFalse();

        verify(userDAO, times(1)).findById(999L);
    }

    @Test
    void getUsersCountShouldReturnCorrectCount() {
        when(userDAO.findAll()).thenReturn(List.of(testUserEntity, new UserEntity("Второй", "second@test.com", 25)));

        long count = userService.getUsersCount();

        assertThat(count).isEqualTo(2);

        verify(userDAO, times(1)).findAll();
    }
}