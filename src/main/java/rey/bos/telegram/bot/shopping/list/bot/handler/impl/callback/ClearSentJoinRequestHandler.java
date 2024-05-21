package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_SENT_JOIN_REQUEST;

@Slf4j
@Component
public class ClearSentJoinRequestHandler extends BotHandlerDecision {

    private final JoinRequestService joinRequestService;
    private final BotUtil botUtil;
    private final UserService userService;

    public ClearSentJoinRequestHandler(
        MessageUtil messageUtil, JoinRequestService joinRequestService, BotUtil botUtil, UserService userService
    ) {
        super(CLEAR_SENT_JOIN_REQUEST, messageUtil);
        this.joinRequestService = joinRequestService;
        this.botUtil = botUtil;
        this.userService = userService;
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        List<JoinRequest> requests = joinRequestService.clearActiveRequest(user.getId());
        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, ACTIVE_JOIN_REQUEST_CLEARED);
        botUtil.executeMethod(message);
        for (JoinRequest request : requests) {
            UserDto owner = userService.findByIdOrThrow(request.getOwnerId());
            EditMessageText messageText = messageUtil.buildEditMessageText(
                owner, request.getMessageId(), JOIN_REQUEST_CANCELLED, messageUtil.getLogin(user.getUserName())
            );
            botUtil.executeMethod(messageText);
        }
        return true;
    }

    @Override
    public boolean handleReject(UserDto user, int messageId, long callbackId) {
        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, REJECT_JOINING_PROCCESS);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
