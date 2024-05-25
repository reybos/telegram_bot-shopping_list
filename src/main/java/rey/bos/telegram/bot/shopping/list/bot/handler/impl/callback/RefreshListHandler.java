package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.ShoppingListHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REFRESH_LIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshListHandler extends BotHandler {

    private final CallBackCommand command = REFRESH_LIST;

    private final ShoppingListHelper shoppingListHelper;
    private final ShoppingListService shoppingListService;

    @Override
    public boolean handle(Update update, User user) {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        shoppingListHelper.refreshUserList(user, messageId, shoppingList);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
