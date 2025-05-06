package com.example.contents.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "view_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Content content;

    private LocalDateTime viewedAt;

    private ViewHistory(User user, Content content, LocalDateTime viewedAt) {
        this.user = user;
        this.content = content;
        this.viewedAt = viewedAt;
    }

    public static ViewHistory of(User user, Content content) {
        return new ViewHistory(user, content, LocalDateTime.now());
    }
}
