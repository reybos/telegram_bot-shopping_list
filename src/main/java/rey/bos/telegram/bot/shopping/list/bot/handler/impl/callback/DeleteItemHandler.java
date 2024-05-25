package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.ShoppingListHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListItemService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteItemHandler extends BotHandler {

    private final CallBackCommand command = DELETE_ITEM;

    private final ShoppingListItemService shoppingListItemService;
    private final ShoppingListService shoppingListService;
    private final ShoppingListHelper shoppingListHelper;

    @Override
    public boolean handle(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        long itemId = Long.parseLong(data.replaceFirst(command.getCommand(), ""));
        shoppingListItemService.deleteItemById(itemId);
        ShoppingList shoppingList;
        try {
            shoppingList = shoppingListService.findActiveList(user.getId());
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }

        shoppingListHelper.refreshUsersList(shoppingList);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
