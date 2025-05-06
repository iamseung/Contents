package com.example.contents.controller;

import com.example.contents.domain.dto.ApiSuccessResponse;
import com.example.contents.domain.dto.PopularPurchaseContentResponse;
import com.example.contents.service.PurchaseHistoryService;
import com.example.contents.service.RankingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/purchase-history")
@RestController
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final RankingCacheService rankingCacheService;

    // 4. 구매 인기 작품 조회, 가장 많이 구매한 작품 상위 10개
    @GetMapping("/popular-content-top10")
    public ResponseEntity<ApiSuccessResponse<List<PopularPurchaseContentResponse>>> getMostPopularPurchaseContentTop10() {
        List<PopularPurchaseContentResponse> response = rankingCacheService.getMostPopularPurchaseContentsTop10LocalCache();
        return ResponseEntity.ok(ApiSuccessResponse.ok(response));
    }

    // 5. 작품 구매 이력 삭제
    @DeleteMapping("/{purchaseHistoryId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deletePurchaseHistory(@PathVariable Long purchaseHistoryId) {
        purchaseHistoryService.deletePurchaseHistory(purchaseHistoryId);
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }
}
