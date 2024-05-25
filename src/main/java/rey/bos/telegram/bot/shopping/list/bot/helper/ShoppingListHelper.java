package rey.bos.telegram.bot.shopping.list.bot.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.repository.params.MessageParams;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DELETE_ITEM;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REFRESH_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.helper.RefreshButtonIcon.REFRESH_BUTTON_COLOR;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

@Component
@RequiredArgsConstructor
public class ShoppingListHelper {

    private final BotUtil botUtil;
    private final MessageShoppingListService messageShoppingListService;
    private final MessageUtil messageUtil;

    public SendMessage buildShoppingListSendMessage(User user, ShoppingList shoppingList) {
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
            messageUtil.buildButton(
                languageCode, REFRESH_LIST_BUTTON, REFRESH_LIST.getCommand(),
                REFRESH_BUTTON_COLOR.get(new Random().nextInt(REFRESH_BUTTON_COLOR.size()))
            )
        ));
        List<ShoppingListItem> items = new ArrayList<>(shoppingList.getItems());
        Collections.sort(items);
        for (ShoppingListItem item : items) {
            rows.add(new InlineKeyboardRow(
                messageUtil.buildButton(item.getValue(), DELETE_ITEM.getCommand() + item.getId())
            ));
        }
        return rows;
    }

    public void refreshUsersList(ShoppingList shoppingList) {
        List<MessageParams> messages = messageShoppingListService.findAllMessageByList(shoppingList.getId());
        messages.forEach(msg -> refreshListMessage(shoppingList, msg));
    }

    public void refreshUserList(User user, int messageId, ShoppingList shoppingList) {
        refreshListMessage(
            shoppingList,
            MessageParams.builder()
                .messageId(messageId)
                .languageCode(user.getLanguageCode())
                .telegramId(user.getTelegramId())
                .build()
        );
    }

    private void refreshListMessage(ShoppingList shoppingList, MessageParams messageParams) {
        if (shoppingList.getItems().isEmpty()) {
            EditMessageText message = messageUtil.buildEditMessageText(
                messageParams.getTelegramId(), messageParams.getLanguageCode(), messageParams.getMessageId(),
                EMPTY_LIST_MESSAGE
            );
            botUtil.executeMethod(message);
        } else {
            EditMessageText editMessage = buildShoppingListEditMessage(messageParams, shoppingList);
            botUtil.executeMethod(editMessage);
        }
    }

    public EditMessageText buildShoppingListEditMessage(MessageParams message, ShoppingList shoppingList) {
        return messageUtil.buildEditMessageTextWithButtons(
            message.getTelegramId(), message.getLanguageCode(), message.getMessageId(), NOT_EMPTY_LIST_MESSAGE,
            buildItems(shoppingList, message.getLanguageCode())
        );
    }

}
