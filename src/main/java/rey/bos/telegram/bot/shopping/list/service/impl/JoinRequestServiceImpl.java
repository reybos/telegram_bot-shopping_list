package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;

    @Override
    public List<JoinRequestParams> findActiveJoinRequest(long userId) {
        return joinRequestRepository.findActiveJoinRequest(userId);
    }

    @Override
    public void createJoinRequest(long userId, long ownerId, int messageId) {
        joinRequestRepository.save(
            JoinRequest.builder()
                .userId(userId)
                .ownerId(ownerId)
                .messageId(messageId)
                .approved(false)
                .expired(false)
                .rejected(false)
                .build()
        );
    }

}
