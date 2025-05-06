package com.example.contents.service;

import com.example.contents.domain.dto.CursorPaginationResponse;
import com.example.contents.domain.dto.ViewHistoryResponse;
import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.ContentRedisEntity;
import com.example.contents.domain.entity.User;
import com.example.contents.domain.entity.ViewHistory;
import com.example.contents.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewHistoryService {

    private final ContentCacheService contentCacheService;
    private final ViewHistoryRepository viewHistoryRepository;

    @Transactional
    public ViewHistory viewContent(User user, Content content) {
        return viewHistoryRepository.save(ViewHistory.of(user, content));
    }

    @Transactional(readOnly = true)
    public CursorPaginationResponse<ViewHistoryResponse> getViewHistories(Long contentId, Long cursorId, int size) {
        ContentRedisEntity content = contentCacheService.getContentLocalCache(contentId);

        Pageable pageable = PageRequest.of(0, size);

        List<ViewHistory> histories = viewHistoryRepository.findByContentIdWithCursor(content.getId(), cursorId, pageable);

        List<ViewHistoryResponse> responses = histories.stream()
                .map(ViewHistoryResponse::from)
                .collect(Collectors.toList());

        Long nextCursorId = responses.isEmpty() ? null : responses.get(responses.size() - 1).getId();

        return new CursorPaginationResponse<>(responses, nextCursorId);
    }

    // 단일 삭제
    @Transactional
    public void deleteViewHistory(Long viewHistoryId) {
        viewHistoryRepository.deleteById(viewHistoryId);
    }

    // 전체 삭제, contentId
    @Transactional
    public void deleteAllByContentId(Long contentId) {
        viewHistoryRepository.deleteAllByContentId(contentId);
    }
}
