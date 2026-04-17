package com.project.userService.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void defaultConstructorShouldInitializeCreatedAt() {
        UserEntity user = new UserEntity();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getAge()).isNull();
    }

    @Test
    void parameterizedConstructorShouldSetAllFields() {
        String name = "Иван Петров";
        String email = "ivan@test.com";
        Integer age = 25;

        UserEntity user = new UserEntity(name, email, age);

        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getAge()).isEqualTo(age);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getId()).isNull();
    }

    @Test
    void settersShouldUpdateFieldsCorrectly() {
        UserEntity user = new UserEntity();
        Long id = 1L;
        String name = "Анна Иванова";
        String email = "anna@test.com";
        Integer age = 30;
        LocalDateTime now = LocalDateTime.now();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreatedAt(now);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getAge()).isEqualTo(age);
        assertThat(user.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toStringShouldReturnFormattedString() {
        UserEntity user = new UserEntity("Тестовый Пользователь", "test@test.com", 25);
        user.setId(1L);

        String result = user.toString();

        assertThat(result).contains("id=1");
        assertThat(result).contains("name='Тестовый Пользователь'");
        assertThat(result).contains("email='test@test.com'");
        assertThat(result).contains("age=25");
    }
}