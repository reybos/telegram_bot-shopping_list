package rey.bos.telegram.bot.shopping.list.factory;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.time.Clock;
import java.time.Instant;
import java.util.Random;

@Component
@Profile("stub")
@RequiredArgsConstructor
public class JoinRequestFactory {

    private final UserFactory userFactory;
    private final JoinRequestRepository joinRequestRepository;
    private final Clock clock;

    public JoinRequest create(JoinRequestParams requestParams) {
        if (requestParams.getUserId() == null) {
            UserDto from = userFactory.createUser();
            requestParams.setUserId(from.getId());
        }
        if (requestParams.getOwnerId() == null) {
            UserDto to = userFactory.createUser();
            requestParams.setOwnerId(to.getId());
        }
        return joinRequestRepository.save(
            JoinRequest.builder()
                .userId(requestParams.userId)
                .ownerId(requestParams.ownerId)
                .messageId(requestParams.getMessageId())
                .approved(requestParams.isApproved())
                .expired(requestParams.isExpired())
                .rejected(requestParams.rejected)
                .createdAt(requestParams.getCreatedAt() == null ? Instant.now(clock) : requestParams.getCreatedAt())
                .build()
        );
    }

    @Builder
    @Getter
    @Setter
    public static class JoinRequestParams {

        private Long userId;

        private Long ownerId;

        @Builder.Default
        private boolean approved = false;

        @Builder.Default
        private boolean expired = false;

        @Builder.Default
        private boolean rejected = false;

        @Builder.Default
        private int messageId = new Random().nextInt();

        private Instant createdAt;

    }

}
