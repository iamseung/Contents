package com.example.contents.component;

import com.example.contents.domain.dto.ContentDeleteEvent;
import com.example.contents.service.ContentCacheService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ContentEventListener {

    private final ContentCacheService contentCacheService;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void deleteContent(ContentDeleteEvent event) {
        contentCacheService.evictContentCache(event.getContentId());
        contentCacheService.evictContentLocalCache(event.getContentId());
        log.info("content cache delete, contentId : %s".formatted(event.getContentId()));
    }
}
