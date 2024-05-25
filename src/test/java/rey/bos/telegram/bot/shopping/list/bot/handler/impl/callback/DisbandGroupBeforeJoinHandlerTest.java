package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.factory.VerifyMessage;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.ERROR_MEMBER_CANT_DISBAND_GROUP;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class DisbandGroupBeforeJoinHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private DisbandGroupBeforeJoinHandler disbandGroupBeforeJoinHandler;
    @Autowired
    private UserShoppingListFactory userShoppingListFactory;
    @Autowired
    private UserShoppingListService userShoppingListService;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private BotUtil botUtil;

    @Test
    public void whenDisbandGroupThenSuccess() {
        User user = userFactory.createUser();
        User owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);
        UserShoppingList userActiveList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(userActiveList.isOwner()).isFalse();
        disbandGroupBeforeJoinHandler.handleAccept(owner, -1, -1);
        userActiveList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(userActiveList.isOwner()).isTrue();
    }

    @Test
    public void whenDisbandNotOwnGroupThenError() throws TelegramApiException {
        User user = userFactory.createUser();
        User owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);

        disbandGroupBeforeJoinHandler.handleAccept(user, -1, -1);

        EditMessageText messageText = VerifyMessage.getVerifyEditMessageText(telegramClient);
        assertThat(messageText.getText()).isEqualTo(
            botUtil.getText(owner.getLanguageCode(), ERROR_MEMBER_CANT_DISBAND_GROUP)
        );
    }

}