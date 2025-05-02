package com.example.contents.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    @Setter private String password;

    @Column(nullable = false)
    private boolean isAdult;

    private User(Long id, String userId, String password) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.isAdult = false;
    }

    @Builder
    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.isAdult = false;

    }

    public static User of(Long id, String userId, String password) {
        return new User(id, userId, password);
    }

    public static User of(String userId, String password) {
        return new User(null, userId, password);
    }
}