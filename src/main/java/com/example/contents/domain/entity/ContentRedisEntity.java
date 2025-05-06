package com.example.contents.domain.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentRedisEntity {
    private Long id;
    private String title;
    private String description;
    private boolean adult;
    private boolean free;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime eventStartAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime eventEndAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public ContentRedisEntity(Long id, String title, String description, boolean isAdult, boolean isFree, LocalDateTime eventStartAt, LocalDateTime eventEndAt, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.adult = isAdult;
        this.free = isFree;
        this.eventStartAt = eventStartAt;
        this.eventEndAt = eventEndAt;
        this.createdAt = createdAt;
    }

    public ContentRedisEntity(Content content) {
        this(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.isAdult(),
                content.isFree(),
                content.getEventStartAt(),
                content.getEventEndAt(),
                content.getCreatedAt()
        );
    }

    public boolean isCanAccess(User user) {
        return !adult ? true : user.isAdult();
    }

    // 유료일 경우, 구매 수단 및 방법에 대한 로직 추가
    public boolean isFree() {
        LocalDateTime now = LocalDateTime.now();

        // 이벤트 기간이 없으면 기본 유료/무료 여부에 따름
        if (eventStartAt == null || eventEndAt == null) {
            return free;
        }

        return now.isAfter(eventStartAt) && now.isBefore(eventEndAt);
    }
}
