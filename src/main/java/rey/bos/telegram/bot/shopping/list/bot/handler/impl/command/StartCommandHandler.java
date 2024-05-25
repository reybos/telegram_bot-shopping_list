package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_START;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.GREETING_FOR_START;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.HELP_COMMAND_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommandHandler extends BotHandler {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        logCall(user.getId(), MENU_COMMAND_START.getCommand(), "");
        String login = messageUtil.getLogin(user.getUserName());
        String text = botUtil.getText(user.getLanguageCode(), GREETING_FOR_START)
            .formatted(login, botUtil.getText(user.getLanguageCode(), HELP_COMMAND_MESSAGE));
        botUtil.sendMessage(user.getTelegramId(), text);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_START);
    }

}
