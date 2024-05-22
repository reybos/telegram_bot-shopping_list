package rey.bos.telegram.bot.shopping.list.cron;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.VerifyMessage;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.cron.ClearJoinRequestExecutor.HOURS_BEFORE_EXPIRE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.JOIN_REQUEST_EXPIRED_OWNER_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.JOIN_REQUEST_EXPIRED_SENDER_MESSAGE;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class ClearJoinRequestExecutorTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ClearJoinRequestExecutor clearJoinRequestExecutor;
    @Autowired
    private JoinRequestFactory joinRequestFactory;
    @Autowired
    private Clock clock;
    @Autowired
    private JoinRequestService joinRequestService;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private BotUtil botUtil;
    @Autowired
    private MessageUtil messageUtil;

    @Test
    public void whenRunThenCLearOldRequests() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String userLogin = messageUtil.getLogin(user.getUserName());
        UserDto owner = userFactory.createUser();
        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(user.getId())
                .ownerId(owner.getId())
                .createdAt(Instant.now(clock).minus(HOURS_BEFORE_EXPIRE + 1, ChronoUnit.HOURS))
                .build()
        );
        List<JoinRequestParams> requests = joinRequestService.findActiveJoinRequest(user.getId());
        assertThat(requests.size()).isEqualTo(1);

        clearJoinRequestExecutor.execute();

        requests = joinRequestService.findActiveJoinRequest(user.getId());
        assertThat(requests.size()).isEqualTo(0);

        EditMessageText ownerMessage = VerifyMessage.getVerifyEditMessageText(telegramClient);
        assertThat(ownerMessage.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), JOIN_REQUEST_EXPIRED_OWNER_MESSAGE).formatted(userLogin)
        );

        SendMessage userMessage = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(userMessage.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), JOIN_REQUEST_EXPIRED_SENDER_MESSAGE).formatted(ownerLogin)
        );
    }

}