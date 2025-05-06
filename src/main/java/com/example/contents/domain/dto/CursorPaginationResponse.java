package com.example.contents.domain.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CursorPaginationResponse<T> {

    private List<T> items;
    private Long nextCursorId;

    public CursorPaginationResponse(List<T> items, Long nextCursorId) {
        this.items = items;
        this.nextCursorId = nextCursorId;
    }

    public List<T> getItems() {
        return items;
    }

    public Long getNextCursorId() {
        return nextCursorId;
    }
}