package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_CLEAR_LIST;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.CLEAR_LIST_COMMAND_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClearListCommandHandler extends BotHandler {

    private final MessageUtil messageUtil;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, User user) {
        logCall(user.getId(), MENU_COMMAND_CLEAR_LIST.getCommand(), "");
        SendMessage message = messageUtil.buildSendMessageWithButtons(
            user, CLEAR_LIST_COMMAND_MESSAGE, messageUtil.buildYesNoButtons(user, CLEAR_LIST)
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_CLEAR_LIST);
    }

}
