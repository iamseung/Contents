package com.example.contents.service;

import com.example.contents.domain.dto.ContentDeleteEvent;
import com.example.contents.domain.dto.UserDto;
import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.User;
import com.example.contents.exception.BaseException;
import com.example.contents.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.contents.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;

    private final ViewHistoryService viewHistoryService;
    private final PurchaseHistoryService purchaseHistoryService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Content findContentById(Long contentId) {
        return contentRepository.findById(contentId).orElseThrow(
                () -> new BaseException(NOT_EXIST_CONTENT, "존재하지 않는 작품입니다. contentId : %s".formatted(contentId))
        );
    }

    @Transactional
    public void viewContent(Long contentId, UserDto userDto) {
        Content content = findContentById(contentId);
        User user = userService.findUserById(userDto.getId());

        if(content.isCanAccess(user)) {
            throw new BaseException(CANT_BUY_CONTENT, "해당 작품은 성인 회원만 조회할 수 있습니다. contentId : %s".formatted(contentId));
        }

        viewHistoryService.viewContent(user, content);
    }

    // 3. 작품 구매, 성인 작품 고려
    @Transactional
    public void purchaseContent(Long contentId, UserDto userDto) {
        Content content = findContentById(contentId);
        User user = userService.findUserById(userDto.getId());

        if(!content.isCanAccess(user)) {
            throw new BaseException(CANT_BUY_CONTENT, "해당 작품은 성인 회원만 구매할 수 있습니다. contentId : %s".formatted(contentId));
        }

        if(!content.isFree()) {
            throw new BaseException(CANT_BUY_CONTENT, "해당 작품은 유료입니다. contentId : %s".formatted(contentId));
        }

        purchaseHistoryService.createPurchaseHistory(user, content);
    }

    // 5. 작품 삭제
    @Transactional
    public void deleteContent(Long contentId) {
        // 연관 관계 삭제가 아닌 일괄 삭제 진행
        viewHistoryService.deleteAllByContentId(contentId);
        purchaseHistoryService.deleteAllByContentId(contentId);

        contentRepository.deleteById(contentId);

        // 캐시 삭제 이벤트 발행
        eventPublisher.publishEvent(new ContentDeleteEvent(contentId));
    }
}
