package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final Clock clock;

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
                .createdAt(Instant.now(clock))
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

    @Override
    public List<JoinRequest> expireRequests(int hoursBeforeExpire) {
        List<JoinRequest> expiredRequests = joinRequestRepository.findExpiredRequests(
            Instant.now(clock).minus(hoursBeforeExpire, ChronoUnit.HOURS)
        );
        if (CollectionUtils.isEmpty(expiredRequests)) {
            return List.of();
        }
        expiredRequests.forEach(req -> req.setExpired(true));
        joinRequestRepository.saveAll(expiredRequests);
        return expiredRequests;
    }

}
