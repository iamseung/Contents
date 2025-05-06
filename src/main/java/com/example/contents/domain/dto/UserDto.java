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
    private boolean isAdult;

    public static UserDto of(Long id, String userId, String password, boolean isAdult) {
        return new UserDto(id, userId, password, isAdult);
    }

    public static UserDto of(String userId, String password, boolean isAdult) {
        return new UserDto(null, userId, password, isAdult);
    }

    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUserId(), user.getPassword(), user.isAdult());
    }

    public User toEntity() {
        return User.of(userId, password, isAdult);
    }
}
