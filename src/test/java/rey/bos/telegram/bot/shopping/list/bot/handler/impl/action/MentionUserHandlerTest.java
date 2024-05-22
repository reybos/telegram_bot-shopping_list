package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
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
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.factory.VerifyMessage;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.MENTION;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
public class MentionUserHandlerTest {

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
    @Autowired
    private MessageUtil messageUtil;
    @Autowired
    private UserShoppingListFactory userShoppingListFactory;

    @Test
    public void whenTryJoinWithTwoMentionThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        Update update = createUpdateObjectWithJoinCommand(user, "@test", "@test");
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(botUtil.getText(user.getLanguageCode(), ERROR_TOO_MANY_MENTION_IN_JOIN));
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
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(from.getLanguageCode(), ERROR_HAS_ACTIVE_JOIN_REQUEST)
                .formatted(messageUtil.getLogin(to.getUserName()))
        );
    }

    @Test
    public void whenMentionThemSelfThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String mention = "@" + user.getUserName();
        Update update = createUpdateObjectWithJoinCommand(user, mention, null);
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), ERROR_MENTION_THEMSELF).formatted(mention)
        );
    }

    @Test
    public void whenMentionUnknownUserThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String mention = "@test";
        Update update = createUpdateObjectWithJoinCommand(user, mention, null);
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), USER_NOT_EXIST).formatted(mention)
        );
    }

    @Test
    public void whenCurrUserHasOwnActiveGroupThenError() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto member = userFactory.createUser();
        userShoppingListFactory.joinUsersList(member, sender);
        String memberLogin = messageUtil.getLogin(member.getUserName());

        UserDto otherUser = userFactory.createUser();
        String mentionLogin = messageUtil.getLogin(otherUser.getUserName());
        Update update = createUpdateObjectWithJoinCommand(sender, mentionLogin, null);
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(sender.getLanguageCode(), ERROR_SENDER_IS_OWNER_ACTIVE_GROUP)
                .formatted(memberLogin, mentionLogin, memberLogin)
        );
    }

    @Test
    public void whenCurrUserIsMemberActiveGroupThenError() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(sender, owner);
        String ownerLogin = messageUtil.getLogin(owner.getUserName());

        UserDto otherUser = userFactory.createUser();
        String mentionLogin = messageUtil.getLogin(otherUser.getUserName());
        Update update = createUpdateObjectWithJoinCommand(sender, mentionLogin, null);
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(sender.getLanguageCode(), ERROR_SENDER_IS_MEMBER_OF_GROUP)
                .formatted(ownerLogin, mentionLogin)
        );
    }

    @Test
    public void whenSendMsgToMentionUserThenForbidden() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        UserDto mentionUser = userFactory.createUser();
        String mentionLogin = messageUtil.getLogin(mentionUser.getUserName());
        Update update = createUpdateObjectWithJoinCommand(user, mentionLogin, null);
        when(telegramClient.execute(any(SendMessage.class))).thenThrow(
            new TelegramApiRequestException("", new ApiResponse<>(null, HttpStatus.FORBIDDEN.value(), null, null, null))
        );
        shoppingListBot.consume(update);
        SendMessage message = VerifyMessage.getVerifySendMessage(telegramClient);
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), CANT_SEND_MESSAGE).formatted(mentionLogin)
        );
    }

    @Test
    public void whenCurrUserCreateJoinRequestToUserWithoutActiveGroupThenSuccess() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String userLogin = messageUtil.getLogin(user.getUserName());
        UserDto mentionUser = userFactory.createUser();

        String mentionLogin = messageUtil.getLogin(mentionUser.getUserName());
        Update update = createUpdateObjectWithJoinCommand(user, mentionLogin, null);
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        List<SendMessage> messages = VerifyMessage.getVerifySendMessages(telegramClient);

        SendMessage mentionUserMessage = messages.get(messages.size() - 2);
        assertThat(mentionUserMessage.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), OWNER_ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP).formatted(userLogin)
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
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        UserDto member = userFactory.createUser();
        userShoppingListFactory.joinUsersList(owner, member);

        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        Update update = createUpdateObjectWithJoinCommand(sender, ownerLogin, null);
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        List<SendMessage> messages = VerifyMessage.getVerifySendMessages(telegramClient);
        SendMessage mentionUserMessage = messages.get(messages.size() - 2);
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        String memberLogin = messageUtil.getLogin(member.getUserName());
        assertThat(mentionUserMessage.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), OWNER_ACCEPT_JOIN_REQUEST_WITH_ACTIVE_GROUP)
                .formatted(senderLogin, memberLogin)
        );
    }

    @Test
    public void whenCurrUserCreateJoinRequestToUserWithOwnActiveGroupThenSuccess() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        UserDto member = userFactory.createUser();
        userShoppingListFactory.joinUsersList(member, owner);

        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        Update update = createUpdateObjectWithJoinCommand(sender, ownerLogin, null);
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        List<SendMessage> messages = VerifyMessage.getVerifySendMessages(telegramClient);
        SendMessage mentionUserMessage = messages.get(messages.size() - 2);
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        String memberLogin = messageUtil.getLogin(member.getUserName());
        assertThat(mentionUserMessage.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), OWNER_ACCEPT_JOIN_REQUEST_WITH_OWN_ACTIVE_GROUP)
                .formatted(senderLogin, memberLogin)
        );
    }

    private Update createUpdateObjectWithJoinCommand(UserDto userDto, String mention1, String mention2) {
        Update update = new Update();
        Message message = new Message();
        String text = (mention1 == null ? "" : mention1) + (mention2 == null ? "" : " " + mention2);
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
