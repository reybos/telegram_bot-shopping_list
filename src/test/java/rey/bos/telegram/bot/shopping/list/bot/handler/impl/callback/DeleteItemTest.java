package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.ShoppingListItemFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListItemRepository;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
public class DeleteItemTest {

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private ShoppingListItemFactory shoppingListItemFactory;
    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private ShoppingListItemRepository shoppingListItemRepository;
    @Autowired
    private ShoppingListBot shoppingListBot;

    @Test
    public void whenDeleteItemThenSuccess() {
        UserDto user = userFactory.createUser();
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        ShoppingListItem shoppingListItem = shoppingListItemFactory.addItem(shoppingList);

        Update update = createUpdateObjectWithCallback(user, shoppingListItem.getId());
        shoppingListBot.consume(update);

        Optional<ShoppingListItem> storedItem = shoppingListItemRepository.findById(shoppingListItem.getId());
        assertThat(storedItem).isEmpty();
    }

    private Update createUpdateObjectWithCallback(UserDto userDto, long itemId) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(DELETE_ITEM.getCommand() + itemId);
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        callbackQuery.setFrom(user);
        update.setCallbackQuery(callbackQuery);
        return update;
    }

}
