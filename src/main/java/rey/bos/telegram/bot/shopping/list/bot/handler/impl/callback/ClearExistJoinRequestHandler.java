package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_EXIST_JOIN_REQUEST;

@Slf4j
@Component
public class ClearExistJoinRequestHandler extends BotHandlerDecision {

    public ClearExistJoinRequestHandler(MessageUtil messageUtil) {
        super(CLEAR_EXIST_JOIN_REQUEST, messageUtil);
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
