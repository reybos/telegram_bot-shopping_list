package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_JOIN_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinRequestCommandHandler extends BotHandler {

    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        botUtil.sendMessageByKey(
            user.getTelegramId(), user.getLanguageCode(), DictionaryKey.JOIN_COMMAND_MESSAGE
        );
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_JOIN_USER);
    }

}
