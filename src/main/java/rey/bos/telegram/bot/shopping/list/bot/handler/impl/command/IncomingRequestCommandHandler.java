package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.IncomingRequestCommandHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.INCOMING_REQUEST_SETTING;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_INCOMING_REQUEST_SETTING;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.INCOMING_REQUEST_SETTING_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomingRequestCommandHandler extends BotHandler {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;
    private final IncomingRequestCommandHelper requestCommandHelper;

    @Override
    public boolean handle(Update update, User user) {
        logCall(user.getId(), MENU_COMMAND_INCOMING_REQUEST_SETTING.getCommand(), "");
        SendMessage message = messageUtil.buildSendMessageWithButtons(
            user, INCOMING_REQUEST_SETTING_MESSAGE, messageUtil.buildYesNoButtons(user, INCOMING_REQUEST_SETTING),
            requestCommandHelper.getConditionMessage(user), requestCommandHelper.getProposalMessage(user)
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_INCOMING_REQUEST_SETTING);
    }

}
