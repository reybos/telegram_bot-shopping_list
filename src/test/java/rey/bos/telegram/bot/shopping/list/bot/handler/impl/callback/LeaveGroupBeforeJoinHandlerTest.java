package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.ERROR_OWNER_CANT_LEAVE_GROUP;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class LeaveGroupBeforeJoinHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private LeaveGroupBeforeJoinHandler leaveGroupBeforeJoinHandler;
    @Autowired
    private UserShoppingListFactory userShoppingListFactory;
    @Autowired
    private UserShoppingListService userShoppingListService;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private BotUtil botUtil;

    @Test
    public void whenLeaveGroupThenSuccess() {
        UserDto user = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);
        UserShoppingList activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isFalse();
        leaveGroupBeforeJoinHandler.handleAccept(user, -1, -1);
        activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isTrue();
    }

    @Test
    public void whenLeaveOwnGroupThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);

        leaveGroupBeforeJoinHandler.handleAccept(owner, -1, -1);

        ArgumentCaptor<EditMessageText> messageCapture = ArgumentCaptor.forClass(EditMessageText.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        EditMessageText messageText = messageCapture.getValue();
        assertThat(messageText.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), ERROR_OWNER_CANT_LEAVE_GROUP)
        );
    }

}