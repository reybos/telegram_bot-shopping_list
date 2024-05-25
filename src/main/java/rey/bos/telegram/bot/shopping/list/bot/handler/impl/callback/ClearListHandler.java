package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.helper.ShoppingListHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_LIST;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.CLEAR_LIST_ACCEPTED;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.CLEAR_LIST_REJECTED;

@Slf4j
@Component
public class ClearListHandler extends BotHandlerDecision {

    private final BotUtil botUtil;
    private final ShoppingListService shoppingListService;
    private final ShoppingListHelper shoppingListHelper;

    public ClearListHandler(
        MessageUtil messageUtil, BotUtil botUtil, ShoppingListService shoppingListService,
        ShoppingListHelper shoppingListHelper
    ) {
        super(CLEAR_LIST, messageUtil);
        this.botUtil = botUtil;
        this.shoppingListService = shoppingListService;
        this.shoppingListHelper = shoppingListHelper;
    }

    @Override
    public boolean handleAccept(User user, int messageId, long callbackId) {
        try {
            shoppingListService.clearActiveList(user.getId());
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        shoppingListHelper.refreshUsersList(shoppingList);

        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, CLEAR_LIST_ACCEPTED);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean handleReject(User user, int messageId, long callbackId) {
        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, CLEAR_LIST_REJECTED);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
