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
import rey.bos.telegram.bot.shopping.list.factory.ShoppingListItemFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class ClearListHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ClearListHandler clearListHandler;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private ShoppingListItemFactory shoppingListItemFactory;
    @Autowired
    private ShoppingListService shoppingListService;

    @Test
    public void whenClearListThenSuccess() {
        UserDto userDto = userFactory.createUser();
        ShoppingList shoppingList = shoppingListService.findActiveList(userDto.getId());
        shoppingListItemFactory.addItem(shoppingList);
        shoppingListItemFactory.addItem(shoppingList);
        shoppingList = shoppingListService.findActiveList(userDto.getId());
        assertThat(shoppingList.getItems().size()).isEqualTo(2);

        Update update = createUpdateObjectWithCallback(userDto);
        clearListHandler.handle(update, userDto);
        shoppingList = shoppingListService.findActiveList(userDto.getId());
        assertThat(shoppingList.getItems()).isEmpty();
    }

    private Update createUpdateObjectWithCallback(UserDto userDto) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        String data = CLEAR_LIST.getCommand() + userDto.getId() + CONFIRM.getCommand();
        callbackQuery.setData(data);
        Message message = new Message();
        message.setMessageId(new Random().nextInt());
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}