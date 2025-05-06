package com.example.contents.domain.dto;

import com.example.contents.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank private String userId;
    @NotBlank private String password;

    public User toEntity() {
        return User.of(userId, password, false);
    }

    public UserDto toDto() {
        return UserDto.of(userId, password, false);
    }
}
