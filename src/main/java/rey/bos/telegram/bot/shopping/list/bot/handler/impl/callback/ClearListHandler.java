package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.ClearListHelper;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CLEAR_LIST_ACCEPTED;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CLEAR_LIST_REJECTED;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_LIST;

@Slf4j
@Component
public class ClearListHandler extends BotHandlerDecision {

    private final BotUtil botUtil;
    private final ShoppingListService shoppingListService;
    private final ClearListHelper clearListHelper;

    public ClearListHandler(
        MessageUtil messageUtil, BotUtil botUtil, ShoppingListService shoppingListService,
        ClearListHelper clearListHelper
    ) {
        super(CLEAR_LIST, messageUtil);
        this.botUtil = botUtil;
        this.shoppingListService = shoppingListService;
        this.clearListHelper = clearListHelper;
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        try {
            shoppingListService.clearActiveList(user.getId());
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        EditMessageText message = clearListHelper.buildMessage(user, messageId, CLEAR_LIST_ACCEPTED);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean handleReject(UserDto user, int messageId, long callbackId) {
        EditMessageText message = clearListHelper.buildMessage(user, messageId, CLEAR_LIST_REJECTED);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
