package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<JoinRequest> rejectRequest(long ownerId, int messageId) {
        Optional<JoinRequest> joinRequestO = joinRequestRepository.findByOwnerIdAndMessageId(ownerId, messageId);
        if (joinRequestO.isEmpty()) {
            return joinRequestO;
        }
        JoinRequest joinRequest = joinRequestO.get();
        joinRequest.setRejected(true);
        return Optional.of(joinRequestRepository.save(joinRequest));
    }

    @Override
    public Optional<JoinRequest> findRequest(long ownerId, int messageId) {
        return joinRequestRepository.findByOwnerIdAndMessageId(ownerId, messageId);
    }

    @Override
    public List<JoinRequest> clearActiveRequest(long userId) {
        List<JoinRequest> requests = joinRequestRepository.findActiveRequestByUserId(userId);
        requests.forEach(req -> req.setExpired(true));
        joinRequestRepository.saveAll(requests);
        return requests;
    }

}
