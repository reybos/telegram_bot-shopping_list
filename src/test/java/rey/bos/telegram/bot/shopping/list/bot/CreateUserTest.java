package rey.bos.telegram.bot.shopping.list.bot;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class CreateUserTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListBot shoppingListBot;
    @Autowired
    private UserService userService;

    @ParameterizedTest
    @CsvSource({",EN", "en,EN", "ru,RU", "arn,EN"})
    void whenReceiveMessageThenStoreUser(String languageCode, LanguageCode expectedLanguageCode) {
        long telegramId = new Random().nextLong();
        String firstName = RandomStringUtils.randomAlphanumeric(10);
        String userName = RandomStringUtils.randomAlphanumeric(10);
        Update update = createUpdateObjectWithUser(telegramId, languageCode, firstName, userName);
        shoppingListBot.consume(update);
        UserDto storedUser = userService.getOrCreateUser(UserDto.builder().telegramId(telegramId).build());
        UserDto expectedUser = UserDto.builder()
            .userName(userName)
            .languageCode(expectedLanguageCode)
            .firstName(firstName)
            .telegramId(telegramId)
            .build();
        assertThat(storedUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedUser);
    }

    private Update createUpdateObjectWithUser(
        long telegramId, String languageCode, String firstName, String userName
    ) {
        Update update = new Update();
        Message message = new Message();
        User user = new User(telegramId, firstName, false);
        user.setUserName(userName);
        user.setLanguageCode(languageCode);
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}