package rey.bos.telegram.bot.shopping.list.bot;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.BOT_COMMAND;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.MENTION;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_JOIN_USER;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
public class SendJoinRequestTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListBot shoppingListBot;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private BotUtil botUtil;
    @Autowired
    private JoinRequestFactory joinRequestFactory;
    @Autowired
    private JoinRequestService joinRequestService;

    @ParameterizedTest
    @CsvSource({",,ERROR_EMPTY_MENTION_IN_JOIN", "@test,@test2,ERROR_TOO_MANY_MENTION_IN_JOIN"})
    public void whenTryJoinWithNotCorrectMentionThenError(
        String mention1, String mention2, DictionaryKey key
    ) throws TelegramApiException {
        UserDto user = userFactory.createUser();
        Update update = createUpdateObjectWithJoinCommand(user, mention1, mention2);
        shoppingListBot.consume(update);
        SendMessage message = getVerifySendMessage();
        assertThat(message.getText()).isEqualTo(botUtil.getText(user.getLanguageCode(), key));
    }

    private SendMessage getVerifySendMessage() throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

    private List<SendMessage> getVerifySendMessages() throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getAllValues();
    }

    @Test
    public void whenCurrUserHasActiveRequestThenError() throws TelegramApiException {
        UserDto from = userFactory.createUser();
        UserDto to = userFactory.createUser();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(from.getId())
                .ownerId(to.getId())
                .build()
        );
        Update update = createUpdateObjectWithJoinCommand(from, "@mention", null);
        shoppingListBot.consume(update);
        SendMessage message = getVerifySendMessage();
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(from.getLanguageCode(), ERROR_HAS_JOIN_REQUEST).formatted("@" + to.getUserName())
        );
    }

    @Test
    public void whenMentionUnknownUserThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String mention = "@test";
        Update update = createUpdateObjectWithJoinCommand(user, mention, null);
        shoppingListBot.consume(update);
        SendMessage message = getVerifySendMessage();
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), USER_NOT_EXIST).formatted(mention)
        );
    }

    @Test
    public void whenCurrUserHasOwnActiveGroupThenError() {
        //todo
    }

    @Test
    public void whenCurrUserMemberActiveGroupThenError() {
        //todo
    }

    @Test
    public void whenSendMsgToMentionUserThenForbidden() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        UserDto mentionUser = userFactory.createUser();
        String mentionLogin = "@" + mentionUser.getUserName();
        Update update = createUpdateObjectWithJoinCommand(user, mentionLogin, null);
        when(telegramClient.execute(any(SendMessage.class))).thenThrow(
            new TelegramApiRequestException("", new ApiResponse<>(null, HttpStatus.FORBIDDEN.value(), null, null, null))
        );
        shoppingListBot.consume(update);
        SendMessage message = getVerifySendMessage();
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), CANT_SEND_MESSAGE).formatted(mentionLogin)
        );
    }

    @Test
    public void whenCurrUserCreateJoinRequestToUserWithoutActiveGroupThenSuccess() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String userLogin = "@" + user.getUserName();
        UserDto mentionUser = userFactory.createUser();
        String mentionLogin = "@" + mentionUser.getUserName();
        Update update = createUpdateObjectWithJoinCommand(user, mentionLogin, null);
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        List<SendMessage> messages = getVerifySendMessages();

        SendMessage mentionUserMessage = messages.get(messages.size() - 2);
        assertThat(mentionUserMessage.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP).formatted(userLogin)
        );

        SendMessage currUserMessage = messages.get(messages.size() - 1);
        assertThat(currUserMessage.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), SEND_JOIN_REQUEST_SUCCESS).formatted(mentionLogin)
        );

        List<JoinRequestParams> requests = joinRequestService.findActiveJoinRequest(user.getId());
        assertThat(requests).anyMatch(req -> req.getOwnerUserName().equals(mentionLogin));
    }

    @Test
    public void whenCurrUserCreateJoinRequestToUserWithActiveGroupThenSuccess() throws TelegramApiException {
        //todo
    }

    @Test
    public void whenCurrUserCreateJoinRequestToUserWithOwnActiveGroupThenSuccess() throws TelegramApiException {
        //todo
    }

    private Update createUpdateObjectWithJoinCommand(UserDto userDto, String mention1, String mention2) {
        Update update = new Update();
        Message message = new Message();
        String text = MENU_COMMAND_JOIN_USER.getCommand() +
            (mention1 == null ? "" : " " + mention1) +
            (mention2 == null ? "" : " " + mention2);
        message.setText(text);
        message.setEntities(buildEntities(mention1, mention2));
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

    private List<MessageEntity> buildEntities(String mention1, String mention2) {
        List<MessageEntity> entities = new ArrayList<>();
        int offset = 0;
        entities.add(
            MessageEntity.builder()
                .type(BOT_COMMAND.getDescription())
                .offset(offset)
                .length(MENU_COMMAND_JOIN_USER.getCommand().length())
                .text(MENU_COMMAND_JOIN_USER.getCommand())
                .build()
        );
        offset += MENU_COMMAND_JOIN_USER.getCommand().length() + 1;
        if (mention1 != null) {
            entities.add(buildMentionEntity(mention1, offset));
            offset += mention1.length() + 1;
        }
        if (mention2 != null) {
            entities.add(buildMentionEntity(mention2, offset));
        }
        return entities;
    }

    private MessageEntity buildMentionEntity(String mention, int offset) {
        return MessageEntity.builder()
            .type(MENTION.getDescription())
            .offset(offset)
            .length(mention.length())
            .text(mention)
            .build();
    }

}
