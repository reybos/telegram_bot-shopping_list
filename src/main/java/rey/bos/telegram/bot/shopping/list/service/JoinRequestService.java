package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;

import java.util.List;

public interface JoinRequestService {

    List<JoinRequestParams> findActiveJoinRequest(long userId);

    void createJoinRequest(long userId, long ownerId, int messageId);

}
