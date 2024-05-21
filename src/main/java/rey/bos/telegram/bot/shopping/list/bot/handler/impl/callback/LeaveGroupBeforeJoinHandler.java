package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP_BEFORE_JOIN;

@Slf4j
@Component
public class LeaveGroupBeforeJoinHandler extends BotHandlerDecision {

    private final BotUtil botUtil;
    private final UserShoppingListService userShoppingListService;
    private final UserService userService;

    public LeaveGroupBeforeJoinHandler(
        MessageUtil messageUtil, BotUtil botUtil, UserShoppingListService userShoppingListService,
        UserService userService
    ) {
        super(LEAVE_GROUP_BEFORE_JOIN, messageUtil);
        this.botUtil = botUtil;
        this.userShoppingListService = userShoppingListService;
        this.userService = userService;
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        UserShoppingList userShoppingList = userShoppingListService.findActiveUserShoppingList(user.getId());
        if (userShoppingList.isOwner()) {
            EditMessageText messageText = messageUtil.buildEditMessageText(
                user, messageId, ERROR_OWNER_CANT_LEAVE_GROUP
            );
            botUtil.executeMethod(messageText);
            return true;
        }
        List<UserShoppingList> group = userShoppingListService.findActiveGroupByUserId(user.getId());
        UserShoppingList ownerList = group.stream().filter(UserShoppingList::isOwner).toList().get(0);
        userShoppingListService.restoreMainList(user.getId());
        EditMessageText messageText = messageUtil.buildEditMessageText(
            user, messageId, LEAVE_GROUP_BEFORE_JOIN_SUCCESS_MESSAGE
        );
        botUtil.executeMethod(messageText);
        UserDto owner = userService.findByIdOrThrow(ownerList.getUserId());
        SendMessage sendMessage = messageUtil.buildSendMessage(
            owner, USER_LEFT_YOUR_GROUP_MESSAGE, messageUtil.getLogin(user.getUserName())
        );
        botUtil.executeMethod(sendMessage);
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
