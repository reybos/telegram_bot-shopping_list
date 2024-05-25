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
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class IncomingRequestSettingHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private IncomingRequestSettingHandler requestSettingHandler;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserService userService;

    @Test
    public void whenChangeIncomingRequestSettingThenSuccess() {
        User user = userFactory.createUser();
        assertThat(user.isJoinRequestDisabled()).isFalse();
        requestSettingHandler.handleAccept(user, -1, -1);
        user = userService.findByIdOrThrow(user.getId());
        assertThat(user.isJoinRequestDisabled()).isTrue();
    }

}