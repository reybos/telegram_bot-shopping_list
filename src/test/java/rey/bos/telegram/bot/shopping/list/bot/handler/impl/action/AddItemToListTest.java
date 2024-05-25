package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.ShoppingListItemFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.action.AddItemHandler.MAX_ITEM_LENGTH;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.action.AddItemHandler.MAX_ITEM_NUMBER;

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
    private UserFactory userFactory;
    @Autowired
    private ShoppingListItemFactory shoppingListItemFactory;

    @Test
    public void whenAddItemThenSuccess() {
        User user = userFactory.createUser();
        String itemValue = RandomStringUtils.randomAlphanumeric(10);
        Update update = createUpdateObjectWithItem(user, itemValue);
        shoppingListBot.consume(update);

        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        assertThat(shoppingList.getItems().stream().toList().get(0).getValue()).isEqualTo(itemValue);
    }

    @Test
    public void whenAddTooLongItemThenError() {
        User user = userFactory.createUser();
        String itemValue = RandomStringUtils.randomAlphanumeric(MAX_ITEM_LENGTH + 1);
        Update update = createUpdateObjectWithItem(user, itemValue);
        shoppingListBot.consume(update);

        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        assertThat(shoppingList.getItems()).isEmpty();
    }

    @Test
    public void whenAddTooManyItemThenError() {
        User user = userFactory.createUser();
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        for (int i = 0; i < MAX_ITEM_NUMBER; i++) {
            shoppingListItemFactory.addItem(shoppingList);
        }
        Update update = createUpdateObjectWithItem(user, "not added");
        shoppingListBot.consume(update);

        assertThat(shoppingList.getItems()).noneMatch(item -> item.getValue().equals("not added"));
    }

    private Update createUpdateObjectWithItem(User storedUser, String item) {
        Update update = new Update();
        Message message = new Message();
        message.setText(item);
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User(
            storedUser.getTelegramId(), storedUser.getFirstName(), false
        );
        user.setUserName(storedUser.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}