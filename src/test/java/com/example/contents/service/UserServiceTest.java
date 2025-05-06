package com.example.contents.service;

import com.example.contents.domain.dto.UserDto;
import com.example.contents.domain.entity.User;
import com.example.contents.exception.BaseException;
import com.example.contents.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.contents.exception.ErrorCode.DUPLICATE_USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 사용자")

class UserServiceTest {

    @InjectMocks private UserService sut;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @DisplayName("사용자 정보를 입력하면, 사용자가 생성된다.")
    @Test
    void givenUserInfo_whenCreatingUser_thenSavesUser() {
        // Given
        UserDto dto = createUserDto();
        User user = dto.toEntity();
        given(userRepository.findByUserId(dto.getUserId())).willReturn(Optional.empty());
        given(passwordEncoder.encode(dto.getPassword())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // When & Then
        assertThatCode(() -> sut.createUser(dto)).doesNotThrowAnyException();
        then(userRepository).should().findByUserId(dto.getUserId());
        then(passwordEncoder).should().encode(dto.getPassword());
        then(userRepository).should().save(any(User.class));
    }

    @DisplayName("중복된 사용자 ID로 사용자 생성 시, 예외를 던진다.")
    @Test
    void givenDuplicateUserId_whenCreatingUser_thenThrowsException() {
        // Given
        UserDto dto = createUserDto();
        given(userRepository.findByUserId(dto.getUserId())).willReturn(Optional.of(dto.toEntity()));

        // When & Then
        assertThatThrownBy(() -> sut.createUser(dto))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("중복된 아이디 입니다.")
                .extracting("errorCode")
                .isEqualTo(DUPLICATE_USER_ID);

        then(userRepository).should().findByUserId(dto.getUserId());
        then(passwordEncoder).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    private UserDto createUserDto() {
        return UserDto.of("A", "password", true);
    }
}