package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP_CONFIRM;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.LEAVE_GROUP_CONFIRM_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveGroupConfirmHandler extends BotHandler {

    private final CallBackCommand command = LEAVE_GROUP_CONFIRM;

    private final MessageUtil messageUtil;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, User user) {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        EditMessageText message = messageUtil.buildEditMessageTextWithButtons(
            user, messageId, LEAVE_GROUP_CONFIRM_MESSAGE, messageUtil.buildYesNoButtons(user, LEAVE_GROUP)
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
