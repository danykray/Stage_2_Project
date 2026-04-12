package com.project.userService.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public UserEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public UserEntity(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public Integer getAge() {
        return age;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s', age=%d, createdAt=%s}",
                id, name, email, age, createdAt);
    }
}
