package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.CallBackCommand.DELETE_ITEM;
import static rey.bos.telegram.bot.shopping.list.bot.MenuCommand.MENU_COMMAND_SHOW_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.EMPTY_LIST_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.NOT_EMPTY_LIST_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowListCommandHandler extends BotHandler {

    private final ShoppingListService shoppingListService;
    private final BotUtil botUtil;
    private final TelegramClient telegramClient;
    private final MessageShoppingListService messageShoppingListService;

    @Override
    public boolean handle(Update update, UserDto user) {
        ShoppingList shoppingList;
        try {
            shoppingList = shoppingListService.findActiveList(user.getId());
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        if (shoppingList.getItems().isEmpty()) {
            botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), EMPTY_LIST_MESSAGE);
            return true;
        }
        SendMessage message = buildListMessage(user, shoppingList);
        try {
            Message sentMessage = telegramClient.execute(message);
            messageShoppingListService.saveShoppingListMessage(
                user.getId(), shoppingList.getId(), sentMessage.getMessageId()
            );
        } catch (TelegramApiException e) {
            log.error("Can't execute command", e);
        }
        return true;
    }

    private SendMessage buildListMessage(UserDto user, ShoppingList shoppingList) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), NOT_EMPTY_LIST_MESSAGE))
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

    @Override
    public boolean support(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
            && (!CollectionUtils.isEmpty(update.getMessage().getEntities())
                && update.getMessage().getEntities()
                    .stream()
                    .map(MessageEntity::getType)
                    .anyMatch(BOT_COMMAND_TYPE::equals)
                && update.getMessage().getEntities()
                    .stream()
                    .map(MessageEntity::getText)
                    .anyMatch(MENU_COMMAND_SHOW_LIST.getCommand()::equals)
            );
    }

}
