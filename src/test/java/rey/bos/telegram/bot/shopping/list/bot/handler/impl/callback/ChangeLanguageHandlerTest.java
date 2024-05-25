package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CHANGE_LANGUAGE;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class ChangeLanguageHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private ChangeLanguageHandler changeLanguageHandler;

    @Test
    public void whenChangeLanguageThenSuccess() {
        User user = userFactory.createUser(
            UserFactory.UserParams.builder().languageCode(LanguageCode.RU).build()
        );
        LanguageCode newLanguage = LanguageCode.EN;
        Update update = createUpdateObjectWithCallback(user, newLanguage);
        changeLanguageHandler.handle(update, user);
        User stored = userService.findByIdOrThrow(user.getId());
        assertThat(stored.getLanguageCode()).isEqualTo(newLanguage);
    }

    private Update createUpdateObjectWithCallback(User storesUser, LanguageCode code) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        String data = CHANGE_LANGUAGE.getCommand() + code;
        callbackQuery.setData(data);
        Message message = new Message();
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User(
            storesUser.getTelegramId(), storesUser.getFirstName(), false
        );
        user.setUserName(storesUser.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}