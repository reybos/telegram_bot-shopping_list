package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.JoinRequestRepository;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class ClearSentJoinRequestHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private ClearSentJoinRequestHandler clearSentJoinRequestHandler;
    @Autowired
    private JoinRequestFactory joinRequestFactory;
    @Autowired
    private JoinRequestRepository joinRequestRepository;

    @Test
    public void whenClearSentRequestThenSuccess() {
        UserDto user = userFactory.createUser();
        joinRequestFactory.create(JoinRequestFactory.JoinRequestParams.builder().userId(user.getId()).build());
        List<JoinRequest> requests = joinRequestRepository.findActiveRequestByUserId(user.getId());
        assertThat(requests.size()).isEqualTo(1);

        clearSentJoinRequestHandler.handleAccept(user, -1, -1);

        requests = joinRequestRepository.findActiveRequestByUserId(user.getId());
        assertThat(requests).isEmpty();
    }

}