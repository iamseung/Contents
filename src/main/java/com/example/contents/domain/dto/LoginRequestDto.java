package com.example.contents.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank private String userId;
    @NotBlank private String password;
}