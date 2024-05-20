package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DISBAND_GROUP_AND_JOIN;

@Slf4j
@Component
public class DisbandGroupAndJoinHandler extends BotHandlerDecision {

    public DisbandGroupAndJoinHandler(MessageUtil messageUtil) {
        super(DISBAND_GROUP_AND_JOIN, messageUtil);
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        return true;
    }

    @Override
    public boolean handleReject(UserDto user, int messageId, long callbackId) {
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}