package com.project.userService.service;

import com.project.userService.dto.UserRequestDto;
import com.project.userService.dto.UserResponseDto;
import com.project.userService.entity.UserEntity;
import com.project.userService.mapper.UserMapper;
import com.project.userService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private UserRequestDto requestDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Иван Петров");
        userEntity.setEmail("ivan@test.com");
        userEntity.setAge(25);
        userEntity.setCreatedAt(LocalDateTime.now());

        requestDto = new UserRequestDto("Иван Петров", "ivan@test.com", 25);

        responseDto = new UserResponseDto(1L, "Иван Петров", "ivan@test.com", 25, LocalDateTime.now());
    }

    @Test
    void createUser_ShouldReturnUserResponseDto_WhenDataIsValid() {
        when(userRepository.existsByEmail("ivan@test.com")).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(userEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Иван Петров");
        assertThat(result.getEmail()).isEqualTo("ivan@test.com");

        verify(userRepository, times(1)).existsByEmail("ivan@test.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("ivan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with email ivan@test.com already exists");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Иван Петров");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with id 999 not found");
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Иван Петров");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenDataIsValid() {
        UserRequestDto updateRequest = new UserRequestDto("Иван Сидоров", "ivan.sidorov@test.com", 26);
        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("Иван Сидоров");
        updatedEntity.setEmail("ivan.sidorov@test.com");
        updatedEntity.setAge(26);

        UserResponseDto updatedResponse = new UserResponseDto(1L, "Иван Сидоров", "ivan.sidorov@test.com", 26, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("ivan.sidorov@test.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);
        when(userMapper.toResponseDto(updatedEntity)).thenReturn(updatedResponse);

        UserResponseDto result = userService.updateUser(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Иван Сидоров");
        assertThat(result.getEmail()).isEqualTo("ivan.sidorov@test.com");
        assertThat(result.getAge()).isEqualTo(26);

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User with id 999 not found");

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepository.findByEmail("ivan@test.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserByEmail("ivan@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("ivan@test.com");

        verify(userRepository, times(1)).findByEmail("ivan@test.com");
    }

    @Test
    void getUsersOlderThan_ShouldReturnUsers() {
        when(userRepository.findByAgeGreaterThan(25)).thenReturn(List.of(userEntity));
        when(userMapper.toResponseDto(userEntity)).thenReturn(responseDto);

        List<UserResponseDto> result = userService.getUsersOlderThan(25);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAge()).isEqualTo(25);

        verify(userRepository, times(1)).findByAgeGreaterThan(25);
    }
}