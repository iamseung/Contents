package com.example.contents.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PopularViewContentResponse {
    private Long contentId;
    private String title;
    private Long viewCount;
}
