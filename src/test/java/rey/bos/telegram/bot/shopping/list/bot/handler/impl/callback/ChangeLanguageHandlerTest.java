package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

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
        UserDto userDto = userFactory.createUser(
            UserFactory.UserParams.builder().languageCode(LanguageCode.RU).build()
        );
        LanguageCode newLanguage = LanguageCode.EN;
        Update update = createUpdateObjectWithCallback(userDto, newLanguage);
        changeLanguageHandler.handle(update, userDto);
        UserDto stored = userService.findByIdOrThrow(userDto.getId());
        assertThat(stored.getLanguageCode()).isEqualTo(newLanguage);
    }

    private Update createUpdateObjectWithCallback(UserDto userDto, LanguageCode code) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        String data = CHANGE_LANGUAGE.getCommand() + code;
        callbackQuery.setData(data);
        Message message = new Message();
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}