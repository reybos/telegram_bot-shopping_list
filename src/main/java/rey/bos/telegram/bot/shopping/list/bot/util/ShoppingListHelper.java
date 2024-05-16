package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.params.MessageParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.NOT_EMPTY_LIST_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;

@Component
@RequiredArgsConstructor
public class ShoppingListHelper {

    private final BotUtil botUtil;

    public SendMessage buildShoppingListSendMessage(UserDto user, ShoppingList shoppingList) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), NOT_EMPTY_LIST_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildItems(shoppingList))
                .build())
            .build();
    }

    public EditMessageText buildShoppingListEditMessage(MessageParams message, ShoppingList shoppingList) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(message.getTelegramId())
            .messageId(message.getMessageId())
            .text(botUtil.getText(message.getLanguageCode(), NOT_EMPTY_LIST_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildItems(shoppingList))
                .build())
            .build();
    }

    private List<InlineKeyboardRow> buildItems(ShoppingList shoppingList) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (ShoppingListItem item : shoppingList.getItems()) {
            rows.add(new InlineKeyboardRow(
                InlineKeyboardButton
                    .builder()
                    .text(item.getValue())
                    .callbackData(DELETE_ITEM.getCommand() + item.getId())
                    .build()
            ));
        }
        return rows;
    }

}
