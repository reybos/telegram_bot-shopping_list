package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.factory.VerifyMessage;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

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
    private UserShoppingListFactory userShoppingListFactory;
    @Autowired
    private MessageUtil messageUtil;
    @Autowired
    private UserShoppingListService userShoppingListService;

    @Test
    public void whenAcceptExpiredRequestThenError() throws TelegramApiException {
        User sender = userFactory.createUser();
        User owner = userFactory.createUser();
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

        EditMessageText messageText = VerifyMessage.getVerifyEditMessageText(telegramClient);
        assertThat(messageText.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), CANT_FIND_ACTIVE_JOIN_REQUEST)
        );
    }

    @Test
    public void whenAcceptRequestWithOwnGroupThenSuccess() throws TelegramApiException {
        User sender = userFactory.createUser();
        User owner = userFactory.createUser();
        User member = userFactory.createUser();
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

        EditMessageText ownerMsg = VerifyMessage.getVerifyEditMessageText(telegramClient);
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        assertThat(ownerMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), JOIN_REQUEST_ACCEPTED_OWNER).formatted(senderLogin)
        );
        assertThat(ownerMsg.getMessageId()).isEqualTo(messageId);

        SendMessage senderMsg = VerifyMessage.getVerifySendMessage(telegramClient);
        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        assertThat(senderMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), JOIN_REQUEST_ACCEPTED_SENDER).formatted(ownerLogin)
        );
    }

    @Test
    public void whenAcceptRequestAndMemberOtherGroupThenSuccess() {
        User sender = userFactory.createUser();
        User owner = userFactory.createUser();
        User otherUser = userFactory.createUser();
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
        User sender = userFactory.createUser();
        User owner = userFactory.createUser();
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

        EditMessageText ownerMsg = VerifyMessage.getVerifyEditMessageText(telegramClient);
        String senderLogin = messageUtil.getLogin(sender.getUserName());
        assertThat(ownerMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), OWNER_MSG_JOIN_REQUEST_REJECTED).formatted(senderLogin)
        );
        assertThat(ownerMsg.getMessageId()).isEqualTo(messageId);

        SendMessage senderMsg = VerifyMessage.getVerifySendMessage(telegramClient);
        String ownerLogin = messageUtil.getLogin(owner.getUserName());
        assertThat(senderMsg.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), SENDER_MSG_JOIN_REQUEST_REJECTED).formatted(ownerLogin)
        );
    }

    private Update createUpdateObjectWithCallback(User storedUser, String decision, Integer messageId) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        String data = ACCEPT_JOIN_REQUEST.getCommand() + storedUser.getId() + (decision == null ? "" : decision);
        callbackQuery.setData(data);
        Message message = new Message();
        message.setMessageId(messageId == null ? new Random().nextInt() : messageId);
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User(
            storedUser.getTelegramId(), storedUser.getFirstName(), false
        );
        user.setUserName(storedUser.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}