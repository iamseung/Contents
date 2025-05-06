package com.example.contents.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private boolean isAdult;

    @Column(nullable = false)
    private boolean isFree;

    private LocalDateTime eventStartAt;
    private LocalDateTime eventEndAt;
    private LocalDateTime createdAt;

    public boolean isCanAccess(User user) {
        return !isAdult ? true : user.isAdult();
    }

    // 유료일 경우, 구매 수단 및 방법에 대한 로직 추가
    public boolean isFree() {
        LocalDateTime now = LocalDateTime.now();

        // 이벤트 기간이 없으면 기본 유료/무료 여부에 따름
        if (eventStartAt == null || eventEndAt == null) {
            return isFree;
        }

        return now.isAfter(eventStartAt) && now.isBefore(eventEndAt);
    }

    private Content(String title, String description, boolean isAdult, boolean isFree) {
        this.title = title;
        this.description = description;
        this.isAdult = isAdult;
        this.isFree = isFree;
        this.createdAt = LocalDateTime.now();
    }

    public static Content of(String title, String description, boolean isAdult, boolean isFree) {
        return new Content(title, description, isAdult, isFree);
    }
}
