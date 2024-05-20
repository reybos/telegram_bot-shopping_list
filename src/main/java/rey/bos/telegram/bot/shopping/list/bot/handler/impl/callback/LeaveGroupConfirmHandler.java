package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.GroupHelper;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP_CONFIRM;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveGroupConfirmHandler extends BotHandler {

    private final CallBackCommand command = LEAVE_GROUP_CONFIRM;

    private final GroupHelper groupHelper;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        EditMessageText message = groupHelper.buildLeaveGroupConfirm(user, messageId);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
