package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REMOVE_USER_FROM_GROUP;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

@Slf4j
@Component
public class RemoveUserFromGroupHandler extends BotHandlerDecision {

    private final UserShoppingListService userShoppingListService;
    private final BotUtil botUtil;
    private final UserService userService;

    public RemoveUserFromGroupHandler(
        MessageUtil messageUtil, UserShoppingListService userShoppingListService, BotUtil botUtil,
        UserService userService
    ) {
        super(REMOVE_USER_FROM_GROUP, messageUtil);
        this.userShoppingListService = userShoppingListService;
        this.botUtil = botUtil;
        this.userService = userService;
    }

    @Override
    public boolean handleAccept(User user, int messageId, long callbackId) {
        UserShoppingListGroupParams params = userShoppingListService.getUserListParamsById(callbackId);
        User removedUser = userService.findByIdOrThrow(params.getUserId());
        userShoppingListService.restoreMainList(removedUser.getId());
        EditMessageText message = messageUtil.buildEditMessageText(
            user, messageId, USER_REMOVED_FROM_GROUP_MESSAGE, messageUtil.getLogin(removedUser.getUserName())
        );
        botUtil.executeMethod(message);
        if (!removedUser.isBlocked()) {
            String removedUserMessage = botUtil.getText(removedUser.getLanguageCode(), YOU_REMOVED_FROM_GROUP_MESSAGE)
                .formatted(messageUtil.getLogin(user.getUserName()));
            botUtil.sendMessage(removedUser.getTelegramId(), removedUserMessage);
        }
        return true;
    }

    @Override
    public boolean handleReject(User user, int messageId, long callbackId) {
        UserShoppingListGroupParams params = userShoppingListService.getUserListParamsById(callbackId);
        EditMessageText message = messageUtil.buildEditMessageText(
            user, messageId, REMOVE_FROM_GROUP_CANCEL_MESSAGE, messageUtil.getLogin(params.getUserName())
        );
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
