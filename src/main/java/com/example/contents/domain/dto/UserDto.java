package com.example.contents.domain.dto;

import com.example.contents.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String userId;
    private String password;

    public static UserDto of(Long id, String userId, String password) {
        return new UserDto(id, userId, password);
    }

    public static UserDto of(String userId, String password) {
        return new UserDto(null, userId, password);
    }

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUserId(), user.getPassword());
    }

    public User toEntity() {
        return User.of(userId, password);
    }
}
