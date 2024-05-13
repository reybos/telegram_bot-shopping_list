package rey.bos.telegram.bot.shopping.list.bot;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
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
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.AddItemHandler.MAX_ITEM_LENGTH;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.AddItemHandler.MAX_ITEM_NUMBER;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class AddItemToListTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListBot shoppingListBot;

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private UserService userService;

    @Test
    public void whenAddItemThenSuccess() {
        long telegramId = new Random().nextLong();
        String itemValue = RandomStringUtils.randomAlphanumeric(10);
        Update update = createUpdateObjectWithItem(telegramId, itemValue);
        shoppingListBot.consume(update);
        UserDto storedUser = userService.getOrCreateUser(UserDto.builder().telegramId(telegramId).build());
        ShoppingList shoppingList = shoppingListService.findActiveList(storedUser.getId());
        assertThat(shoppingList.getItems().stream().toList().get(0).getValue()).isEqualTo(itemValue);
    }

    @Test
    public void whenAddTooLongItemThenError() {
        long telegramId = new Random().nextLong();
        String itemValue = RandomStringUtils.randomAlphanumeric(MAX_ITEM_LENGTH + 1);
        Update update = createUpdateObjectWithItem(telegramId, itemValue);
        shoppingListBot.consume(update);
        UserDto storedUser = userService.getOrCreateUser(UserDto.builder().telegramId(telegramId).build());
        ShoppingList shoppingList = shoppingListService.findActiveList(storedUser.getId());
        assertThat(shoppingList.getItems()).isEmpty();
    }

    @Test
    public void whenAddTooManyItemThenError() {
        long telegramId = new Random().nextLong();
        String itemValue = RandomStringUtils.randomAlphanumeric(10);
        Update update = createUpdateObjectWithItem(telegramId, itemValue);
        for (int i = 0; i < MAX_ITEM_NUMBER; i++) {
            shoppingListBot.consume(update);
        }
        update = createUpdateObjectWithItem(telegramId, "not added");
        shoppingListBot.consume(update);
        UserDto storedUser = userService.getOrCreateUser(UserDto.builder().telegramId(telegramId).build());
        ShoppingList shoppingList = shoppingListService.findActiveList(storedUser.getId());
        assertThat(shoppingList.getItems()).noneMatch(item -> item.getValue().equals("not added"));
    }

    private Update createUpdateObjectWithItem(long telegramId, String item) {
        Update update = new Update();
        Message message = new Message();
        message.setText(item);
        User user = new User(telegramId, RandomStringUtils.randomAlphanumeric(10), false);
        user.setUserName(RandomStringUtils.randomAlphanumeric(10));
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}