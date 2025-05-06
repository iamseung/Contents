package com.example.contents.controller;

import com.example.contents.domain.dto.ApiSuccessResponse;
import com.example.contents.domain.dto.CustomUserDetails;
import com.example.contents.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/content")
@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    // 3. 작품 구매
    @PostMapping("{contentId}/purchase")
    public ResponseEntity<ApiSuccessResponse<Void>> purchaseContent(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @PathVariable Long contentId) {
        contentService.purchaseContent(contentId, userDetails.toDto());
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }

    // 작품 조회
    @PostMapping("{contentId}/view")
    public ResponseEntity<ApiSuccessResponse<Void>> viewContent(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable Long contentId) {
        contentService.viewContent(contentId, userDetails.toDto());
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }

    // 5. 작품 삭제
    @DeleteMapping("/{contentId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
        return ResponseEntity.ok(ApiSuccessResponse.ok());
    }
}
