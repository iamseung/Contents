package com.example.contents.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentDeleteEvent {
    private Long contentId;
}
