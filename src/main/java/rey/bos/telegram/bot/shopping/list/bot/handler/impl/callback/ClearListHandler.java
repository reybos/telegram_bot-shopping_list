package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CLEAR_LIST_ACCEPTED;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CLEAR_LIST_REJECTED;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearListHandler extends BotHandler {

    private final BotUtil botUtil;
    private final ShoppingListService shoppingListService;

    @Override
    public boolean handle(Update update, UserDto user) {
        CallbackQuery query = update.getCallbackQuery();
        String command = query.getData();
        int messageId = query.getMessage().getMessageId();
        if (command.endsWith(CONFIRM.getCommand())) {
            try {
                handleAccept(user, messageId);
            } catch (IllegalStateException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        } else {
            handleReject(user, messageId);
        }
        return true;
    }

    private void handleAccept(UserDto user, int messageId) {
        shoppingListService.clearActiveList(user.getId());
        EditMessageText message = buildMessage(user, messageId, CLEAR_LIST_ACCEPTED);
        botUtil.executeMethod(message);
    }

    private void handleReject(UserDto user, int messageId) {
        EditMessageText message = buildMessage(user, messageId, CLEAR_LIST_REJECTED);
        botUtil.executeMethod(message);
    }

    private EditMessageText buildMessage(UserDto user, int messageId, DictionaryKey key) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), key))
            .build();
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, CLEAR_LIST);
    }

}
