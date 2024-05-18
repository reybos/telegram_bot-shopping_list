package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class AcceptJoinRequestHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private AcceptJoinRequestHandler acceptJoinRequestHandler;
    @Autowired
    private JoinRequestFactory joinRequestFactory;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private BotUtil botUtil;
    @Autowired
    UserShoppingListFactory userShoppingListFactory;
    @Autowired
    private MessageUtil messageUtil;
    @Autowired
    UserShoppingListService userShoppingListService;

    @Test
    public void whenAcceptExpiredRequestThenError() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        int messageId = new Random().nextInt();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(sender.getId())
                .ownerId(owner.getId())
                .messageId(messageId)
                .expired(true)
                .build()
        );
        Update update = createUpdateObjectWithCallback(owner, CONFIRM.getCommand(), messageId);
        acceptJoinRequestHandler.handle(update, owner);

        EditMessageText messageText = getVerifyEditMessageText();
        assertThat(messageText.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), CANT_FIND_ACTIVE_JOIN_REQUEST)
        );
    }

    @Test
    public void whenAcceptRequestWithOwnGroupThenSuccess() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        UserDto member = userFactory.createUser();
        userShoppingListFactory.joinUsersList(member, owner);
        int messageId = new Random().nextInt();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(sender.getId())
                .ownerId(owner.getId())
                .messageId(messageId)
                .build()
        );
        Update update = createUpdateObjectWithCallback(owner, CONFIRM.getCommand(), messageId);
        acceptJoinRequestHandler.handle(update, owner);

        EditMessageText ownerMsg = getVerifyEditMessageText();
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        assertThat(ownerMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), JOIN_REQUEST_ACCEPTED_OWNER).formatted(senderLogin)
        );
        assertThat(ownerMsg.getMessageId()).isEqualTo(messageId);

        SendMessage senderMsg = getVerifySendMessage();
        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        assertThat(senderMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), JOIN_REQUEST_ACCEPTED_SENDER).formatted(ownerLogin)
        );
    }

    @Test
    public void whenAcceptRequestAndMemberOtherGroupThenSuccess() {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        UserDto otherUser = userFactory.createUser();
        userShoppingListFactory.joinUsersList(owner, otherUser);
        int messageId = new Random().nextInt();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(sender.getId())
                .ownerId(owner.getId())
                .messageId(messageId)
                .build()
        );
        Update update = createUpdateObjectWithCallback(owner, CONFIRM.getCommand(), messageId);
        acceptJoinRequestHandler.handle(update, owner);

        UserShoppingList ownerList = userShoppingListService.findActiveUserShoppingList(owner.getId());
        assertThat(ownerList.isOwner()).isTrue();
        UserShoppingList otherUserList = userShoppingListService.findActiveUserShoppingList(otherUser.getId());
        assertThat(otherUserList.isOwner()).isTrue();
        assertThat(otherUserList.getShoppingListId()).isNotEqualTo(ownerList.getShoppingListId());
        UserShoppingList senderList = userShoppingListService.findActiveUserShoppingList(sender.getId());
        assertThat(senderList.isOwner()).isFalse();
        assertThat(senderList.getShoppingListId()).isEqualTo(ownerList.getShoppingListId());
    }

    @Test
    public void whenRejectRequestThenSuccess() throws TelegramApiException {
        UserDto sender = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(sender, owner);
        int messageId = new Random().nextInt();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(sender.getId())
                .ownerId(owner.getId())
                .messageId(messageId)
                .build()
        );
        Update update = createUpdateObjectWithCallback(owner, REJECT.getCommand(), messageId);
        acceptJoinRequestHandler.handle(update, owner);

        EditMessageText ownerMsg = getVerifyEditMessageText();
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        assertThat(ownerMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), OWNER_MSG_JOIN_REQUEST_REJECTED).formatted(senderLogin)
        );
        assertThat(ownerMsg.getMessageId()).isEqualTo(messageId);

        SendMessage senderMsg = getVerifySendMessage();
        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        assertThat(senderMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), SENDER_MSG_JOIN_REQUEST_REJECTED).formatted(ownerLogin)
        );
    }

    private Update createUpdateObjectWithCallback(UserDto userDto, String decision, Integer messageId) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        String data = ACCEPT_JOIN_REQUEST.getCommand() + userDto.getId() + (decision == null ? "" : decision);
        callbackQuery.setData(data);
        Message message = new Message();
        message.setMessageId(messageId == null ? new Random().nextInt() : messageId);
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

    private SendMessage getVerifySendMessage() throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

    private EditMessageText getVerifyEditMessageText() throws TelegramApiException {
        ArgumentCaptor<EditMessageText> messageCapture = ArgumentCaptor.forClass(EditMessageText.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

}