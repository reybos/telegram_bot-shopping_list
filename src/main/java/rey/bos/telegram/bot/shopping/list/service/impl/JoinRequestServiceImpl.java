package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

}
