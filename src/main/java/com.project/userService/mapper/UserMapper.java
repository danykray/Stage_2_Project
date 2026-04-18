package com.project.userService.mapper;

import com.project.userService.dto.UserRequestDto;
import com.project.userService.dto.UserResponseDto;
import com.project.userService.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setName(requestDto.getName());
        entity.setEmail(requestDto.getEmail());
        entity.setAge(requestDto.getAge());

        return entity;
    }

    public UserResponseDto toResponseDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getAge(),
                entity.getCreatedAt()
        );
    }
}