package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.params.MessageParams;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REFRESH_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.util.RefreshButtonColor.REFRESH_BUTTON_COLOR;

@Component
@RequiredArgsConstructor
public class ShoppingListHelper {

    private final BotUtil botUtil;
    private final MessageShoppingListService messageShoppingListService;

    public SendMessage buildShoppingListSendMessage(UserDto user, ShoppingList shoppingList) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), NOT_EMPTY_LIST_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildItems(shoppingList, user.getLanguageCode()))
                .build())
            .build();
    }

    private List<InlineKeyboardRow> buildItems(ShoppingList shoppingList, LanguageCode languageCode) {
        new Random().nextInt(9);
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(
            InlineKeyboardButton
                .builder()
                .text(botUtil.getText(languageCode, REFRESH_LIST_BUTTON)
                    .formatted(REFRESH_BUTTON_COLOR.get(new Random().nextInt(REFRESH_BUTTON_COLOR.size()))))
                .callbackData(REFRESH_LIST.getCommand())
                .build()
        ));
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

    public void refreshUsersList(ShoppingList shoppingList) {
        List<MessageParams> messages = messageShoppingListService.findAllMessageByList(shoppingList.getId());
        messages.forEach(msg -> refreshListMessage(shoppingList, msg));
    }

    public void refreshUserList(UserDto userDto, int messageId, ShoppingList shoppingList) {
        refreshListMessage(
            shoppingList,
            MessageParams.builder()
                .messageId(messageId)
                .languageCode(userDto.getLanguageCode())
                .telegramId(userDto.getTelegramId())
                .build()
        );
    }

    private void refreshListMessage(ShoppingList shoppingList, MessageParams messageParams) {
        if (shoppingList.getItems().isEmpty()) {
            EditMessageText message = buildEmptyListEditMessage(messageParams);
            botUtil.executeMethod(message);
        } else {
            EditMessageText editMessage = buildShoppingListEditMessage(messageParams, shoppingList);
            botUtil.executeMethod(editMessage);
        }
    }

    public EditMessageText buildShoppingListEditMessage(MessageParams message, ShoppingList shoppingList) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(message.getTelegramId())
            .messageId(message.getMessageId())
            .text(botUtil.getText(message.getLanguageCode(), NOT_EMPTY_LIST_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildItems(shoppingList, message.getLanguageCode()))
                .build())
            .build();
    }

    public EditMessageText buildEmptyListEditMessage(MessageParams message) {
        return EditMessageText
            .builder()
            .parseMode("HTML")
            .chatId(message.getTelegramId())
            .messageId(message.getMessageId())
            .text(botUtil.getText(message.getLanguageCode(), EMPTY_LIST_MESSAGE))
            .build();
    }

}
