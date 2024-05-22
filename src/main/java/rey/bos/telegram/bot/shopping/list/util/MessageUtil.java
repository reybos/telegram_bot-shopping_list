package rey.bos.telegram.bot.shopping.list.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;

import java.util.List;
import java.util.stream.Collectors;

import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@Component
@AllArgsConstructor
@Slf4j
public class MessageUtil {

    private final BotUtil botUtil;

    public String getLoginsExcludingCurrentUser(List<UserShoppingListGroupParams> group, UserDto user) {
        return group.stream()
            .map(UserShoppingListGroupParams::getUserName)
            .filter(name -> !name.equals(user.getUserName()))
            .map(this::getLogin)
            .collect(Collectors.joining(", "));
    }

    public String getGroupOwnerLogin(List<UserShoppingListGroupParams> group) {
        return group.stream()
            .filter(UserShoppingListGroupParams::isOwner)
            .map(item -> getLogin(item.getUserName()))
            .collect(Collectors.joining());
    }

    public long getIdByText(String text, String command) {
        text = text.replaceFirst(command, "");
        text = text.replaceFirst(CallBackCommand.CONFIRM.getCommand(), "");
        text = text.replaceFirst(CallBackCommand.REJECT.getCommand(), "");
        return !text.isEmpty() ? Long.parseLong(text) : -1;
    }

    public String getLogin(String userName) {
        return "@" + userName;
    }

    public EditMessageText buildEditMessageText(UserDto user, int messageId, DictionaryKey messageKey, Object... args) {
        return buildEditMessageText(user.getTelegramId(), user.getLanguageCode(), messageId, messageKey, args);
    }

    public EditMessageText buildEditMessageText(
        long telegramId, LanguageCode code, int messageId, DictionaryKey messageKey, Object... args
    ) {
        return editMessageTextBuilder(telegramId, code, messageId, messageKey, args).build();
    }

    public EditMessageText buildEditMessageTextWithButtons(
        UserDto user, int messageId, DictionaryKey messageKey, List<InlineKeyboardRow> buttons, Object... args
    ) {
        return buildEditMessageTextWithButtons(
            user.getTelegramId(), user.getLanguageCode(), messageId, messageKey, buttons, args
        );
    }

    public EditMessageText buildEditMessageTextWithButtons(
        long telegramId, LanguageCode code, int messageId, DictionaryKey messageKey,
        List<InlineKeyboardRow> buttons, Object... args
    ) {
        return editMessageTextBuilder(telegramId, code, messageId, messageKey, args)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build())
            .build();
    }

    private EditMessageText.EditMessageTextBuilder<?, ?> editMessageTextBuilder(
        long telegramId, LanguageCode code, int messageId, DictionaryKey messageKey, Object... args
    ) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(telegramId)
            .messageId(messageId)
            .text(botUtil.getText(code, messageKey).formatted(args));
    }

    public SendMessage buildSendMessage(UserDto user, DictionaryKey messageKey, Object... args) {
        return sendMessageTextBuilder(user.getTelegramId(), user.getLanguageCode(), messageKey, args).build();
    }

    public SendMessage buildSendMessageWithButtons(
        UserDto user, DictionaryKey messageKey, List<InlineKeyboardRow> buttons, Object... args
    ) {
        return sendMessageTextBuilder(user.getTelegramId(), user.getLanguageCode(), messageKey, args)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buttons)
                .build())
            .build();
    }

    private SendMessage.SendMessageBuilder<?, ?> sendMessageTextBuilder(
        long telegramId, LanguageCode code, DictionaryKey messageKey, Object... args
    ) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(telegramId)
            .text(botUtil.getText(code, messageKey).formatted(args));
    }

    public List<InlineKeyboardRow> buildYesNoButtons(UserDto user, CallBackCommand command) {
        return buildYesNoButtons(user.getId(), user.getLanguageCode(), command);
    }

    public List<InlineKeyboardRow> buildYesNoButtons(long id, LanguageCode code, CallBackCommand command) {
        return List.of(
            new InlineKeyboardRow(
                buildButton(code, CONFIRM_MSG, command.getCommand() + id + CONFIRM.getCommand()),
                buildButton(code, REJECT_MSG, command.getCommand() + id + REJECT.getCommand())
            )
        );
    }

    public InlineKeyboardButton buildButton(
        LanguageCode code, DictionaryKey messageKey, String callbackData, Object... args
    ) {
        return buildButton(botUtil.getText(code, messageKey).formatted(args), callbackData);
    }

    public InlineKeyboardButton buildButton(String text, String callbackData) {
        return InlineKeyboardButton
            .builder()
            .text(text)
            .callbackData(callbackData)
            .build();
    }

}
