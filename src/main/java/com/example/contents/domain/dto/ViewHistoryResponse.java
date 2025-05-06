package com.example.contents.domain.dto;

import com.example.contents.domain.entity.ViewHistory;

import java.time.LocalDateTime;

public class ViewHistoryResponse {

    private Long id;
    private String userId;
    private LocalDateTime viewedAt;

    public ViewHistoryResponse(Long id, String userId, LocalDateTime viewedAt) {
        this.id = id;
        this.userId = userId;
        this.viewedAt = viewedAt;
    }

    public static ViewHistoryResponse from(ViewHistory entity) {
        return new ViewHistoryResponse(
                entity.getId(),
                entity.getUser().getUserId(),
                entity.getViewedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }
}