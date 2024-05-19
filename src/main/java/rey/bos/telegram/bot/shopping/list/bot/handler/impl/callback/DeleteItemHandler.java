package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.ShoppingListHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListItemService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteItemHandler extends BotHandler {

    private final ShoppingListItemService shoppingListItemService;
    private final ShoppingListService shoppingListService;
    private final MessageShoppingListService messageShoppingListService;
    private final ShoppingListHelper shoppingListHelper;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        String command = update.getCallbackQuery().getData();
        long itemId = Long.parseLong(command.replaceFirst(DELETE_ITEM.getCommand(), ""));
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
        return supportCallbackCommand(update, DELETE_ITEM);
    }

}
