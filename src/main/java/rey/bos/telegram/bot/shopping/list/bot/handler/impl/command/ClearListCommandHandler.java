package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_CLEAR_LIST;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClearListCommandHandler extends BotHandler {

    private final MessageUtil messageUtil;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.CLEAR_LIST_COMMAND);

        SendMessage message = SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, CLEAR_LIST))
                .build())
            .build();

        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_CLEAR_LIST);
    }

}
