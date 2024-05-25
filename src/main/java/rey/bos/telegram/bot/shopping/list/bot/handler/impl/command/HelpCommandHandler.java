package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_HELP;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.HELP_COMMAND_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class HelpCommandHandler extends BotHandler {

    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, User user) {
        logCall(user.getId(), MENU_COMMAND_HELP.getCommand(), "");
        String text = botUtil.getText(user.getLanguageCode(), HELP_COMMAND_MESSAGE);
        botUtil.sendMessage(user.getTelegramId(), text);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_HELP);
    }

}
