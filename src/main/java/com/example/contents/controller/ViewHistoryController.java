package com.example.contents.controller;

import com.example.contents.domain.dto.ApiSuccessResponse;
import com.example.contents.domain.dto.CursorPaginationResponse;
import com.example.contents.domain.dto.PopularViewContentResponse;
import com.example.contents.domain.dto.ViewHistoryResponse;
import com.example.contents.service.RankingCacheService;
import com.example.contents.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/view-history")
@RestController
@RequiredArgsConstructor
public class ViewHistoryController {

    private final ViewHistoryService viewHistoryService;
    private final RankingCacheService rankingCacheService;

    // 1. 작품 조회 이력
    @GetMapping("{contentId}")
    public ResponseEntity<ApiSuccessResponse<CursorPaginationResponse<ViewHistoryResponse>>> getViewHistories(
            @PathVariable Long contentId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        CursorPaginationResponse<ViewHistoryResponse> response = viewHistoryService.getViewHistories(contentId, cursorId, size);
        return ResponseEntity.ok(ApiSuccessResponse.ok(response));
    }

    // 2. 인기 작품 조회, 가장 많이 조회한 작품 상위 10개를 조회
    @GetMapping("/popular-content-top10")
    public ResponseEntity<ApiSuccessResponse<List<PopularViewContentResponse>>> getMostPopularContentTop10() {
        List<PopularViewContentResponse> response = rankingCacheService.getMostPopularContentTop10LocalCache();
        return ResponseEntity.ok(ApiSuccessResponse.ok(response));
    }

    // 5. 작품 조회 이력 삭제
    @DeleteMapping("/{viewHistoryId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteViewHistory(@PathVariable Long viewHistoryId) {
        viewHistoryService.deleteViewHistory(viewHistoryId);
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }
}
