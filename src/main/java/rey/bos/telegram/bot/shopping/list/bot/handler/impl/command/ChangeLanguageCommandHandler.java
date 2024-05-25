package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CHANGE_LANGUAGE;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_CHANGE_LANGUAGE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.EN;
import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.RU;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeLanguageCommandHandler extends BotHandler {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        List<InlineKeyboardRow> buttons = List.of(new InlineKeyboardRow(
            messageUtil.buildButton(user.getLanguageCode(), ENGLISH_LANGUAGE, CHANGE_LANGUAGE.getCommand() + EN),
            messageUtil.buildButton(user.getLanguageCode(), RUSSIAN_LANGUAGE, CHANGE_LANGUAGE.getCommand() + RU)
        ));
        SendMessage message = messageUtil.buildSendMessageWithButtons(user, CHANGE_LANGUAGE_MESSAGE, buttons);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_CHANGE_LANGUAGE);
    }

}
