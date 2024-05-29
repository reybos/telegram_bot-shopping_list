package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.JoinRequestHelper;
import rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_JOIN_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinRequestCommandHandler extends BotHandler {

    private final BotUtil botUtil;
    private final JoinRequestHelper joinRequestHelper;
    private final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        logCall(user.getId(), MENU_COMMAND_JOIN_USER.getCommand(), "");
        String userWithoutLoginMessage = joinRequestHelper.getUserWithoutLoginMessage(user);
        SendMessage message = messageUtil.buildSendMessage(
            user, DictionaryKey.JOIN_COMMAND_MESSAGE, userWithoutLoginMessage
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_JOIN_USER);
    }

}
