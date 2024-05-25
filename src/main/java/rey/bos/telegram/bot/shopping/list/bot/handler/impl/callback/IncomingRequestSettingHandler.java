package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.helper.IncomingRequestCommandHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.INCOMING_REQUEST_SETTING;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.INCOMING_REQUEST_SETTING_CANCEL_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.SWITCHED_JOIN_REQUEST_SETTING_MESSAGE;

@Slf4j
@Component
public class IncomingRequestSettingHandler extends BotHandlerDecision {

    private final BotUtil botUtil;
    private final UserService userService;
    private final IncomingRequestCommandHelper requestCommandHelper;

    public IncomingRequestSettingHandler(
        MessageUtil messageUtil, BotUtil botUtil, UserService userService,
        IncomingRequestCommandHelper requestCommandHelper
    ) {
        super(INCOMING_REQUEST_SETTING, messageUtil);
        this.botUtil = botUtil;
        this.userService = userService;
        this.requestCommandHelper = requestCommandHelper;
    }

    @Override
    public boolean handleAccept(User user, int messageId, long callbackId) {
        user = userService.switchJoinRequestSetting(user.getId());
        EditMessageText message = messageUtil.buildEditMessageText(
            user, messageId, SWITCHED_JOIN_REQUEST_SETTING_MESSAGE, requestCommandHelper.getSwitchResultMessage(user)
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean handleReject(User user, int messageId, long callbackId) {
        EditMessageText message = messageUtil.buildEditMessageText(
            user, messageId, INCOMING_REQUEST_SETTING_CANCEL_MESSAGE
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }
}
