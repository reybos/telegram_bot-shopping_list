package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;

import java.util.List;
import java.util.Optional;

public interface JoinRequestService {

    List<JoinRequestParams> findActiveJoinRequest(long userId);

    void createJoinRequest(long userId, long ownerId, int messageId);

    Optional<JoinRequest> rejectRequest(long ownerId, int messageId);

    Optional<JoinRequest> findRequest(long ownerId, int messageId);

    List<JoinRequest> clearActiveRequest(long userId);

}
