package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CHANGE_LANGUAGE;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_CHANGE_LANGUAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeLanguageCommandHandler extends BotHandler {

    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.CHANGE_LANGUAGE_COMMAND);

        SendMessage message = SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                    new InlineKeyboardRow(
                        InlineKeyboardButton
                            .builder()
                            .text(botUtil.getText(user.getLanguageCode(), DictionaryKey.ENGLISH_LANGUAGE))
                            .callbackData(CHANGE_LANGUAGE.getCommand() + LanguageCode.EN)
                            .build(),
                        InlineKeyboardButton
                            .builder()
                            .text(botUtil.getText(user.getLanguageCode(), DictionaryKey.RUSSIAN_LANGUAGE))
                            .callbackData(CHANGE_LANGUAGE.getCommand() + LanguageCode.RU)
                            .build()
                    )))
                .build())
            .build();

        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_CHANGE_LANGUAGE);
    }

}
