package com.project.userService.controller;


import com.project.userService.dto.UserRequestDto;
import com.project.userService.dto.UserResponseDto;
import com.project.userService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Иван Петров", "ivan@test.com", 25);
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван Петров", "ivan@test.com", 25, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Петров")))
                .andExpect(jsonPath("$.email", is("ivan@test.com")))
                .andExpect(jsonPath("$.age", is(25)));

        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("", "ivan@test.com", 25);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван Петров", "ivan@test.com", 25, LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Петров")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<UserResponseDto> users = Arrays.asList(
                new UserResponseDto(1L, "Иван Петров", "ivan@test.com", 25, LocalDateTime.now()),
                new UserResponseDto(2L, "Мария Иванова", "maria@test.com", 30, LocalDateTime.now())
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Иван Петров")))
                .andExpect(jsonPath("$[1].name", is("Мария Иванова")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Иван Сидоров", "ivan.sidorov@test.com", 26);
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван Сидоров", "ivan.sidorov@test.com", 26, LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Иван Сидоров")))
                .andExpect(jsonPath("$.email", is("ivan.sidorov@test.com")))
                .andExpect(jsonPath("$.age", is(26)));

        verify(userService, times(1)).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(1L, "Иван Петров", "ivan@test.com", 25, LocalDateTime.now());

        when(userService.getUserByEmail("ivan@test.com")).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/email/ivan@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("ivan@test.com")));

        verify(userService, times(1)).getUserByEmail("ivan@test.com");
    }

    @Test
    void getUsersOlderThan_ShouldReturnUsers() throws Exception {
        List<UserResponseDto> users = Arrays.asList(
                new UserResponseDto(2L, "Мария Иванова", "maria@test.com", 30, LocalDateTime.now())
        );

        when(userService.getUsersOlderThan(25)).thenReturn(users);

        mockMvc.perform(get("/api/users/age/25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].age", is(30)));

        verify(userService, times(1)).getUsersOlderThan(25);
    }
}