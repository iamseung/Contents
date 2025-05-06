package com.example.contents.controller;

import com.example.contents.domain.dto.ApiSuccessResponse;
import com.example.contents.domain.dto.LoginRequestDto;
import com.example.contents.domain.dto.SignUpRequestDto;
import com.example.contents.jwt.JwtUtil;
import com.example.contents.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    // 1. 회원 가입 API
    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse<Void>> signup(@Valid @RequestBody SignUpRequestDto dto) {
        userService.createUser(dto.toDto());
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }

    // 2. 로그인 API
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<String>> login(@Valid @RequestBody LoginRequestDto dto) throws AuthenticationException {
        // 사용자 인증
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getPassword()));

        // 토큰 생성, userId 기반
        String token = jwtUtil.generateToken(dto.getUserId());

        return ResponseEntity.ok(ApiSuccessResponse.ok(token));
    }
}
