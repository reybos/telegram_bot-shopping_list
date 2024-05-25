package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.List;
import java.util.Optional;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP_BEFORE_JOIN;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

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
    public boolean handleAccept(User user, int messageId, long callbackId) {
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
        Optional<User> ownerO = userService.findActiveUserById(ownerList.getUserId());
        if (ownerO.isPresent()) {
            SendMessage sendMessage = messageUtil.buildSendMessage(
                ownerO.get(), USER_LEFT_YOUR_GROUP_MESSAGE, messageUtil.getLogin(user.getUserName())
            );
            botUtil.executeMethod(sendMessage);
        }
        return true;
    }

    @Override
    public boolean handleReject(User user, int messageId, long callbackId) {
        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, REJECT_JOINING_PROCCESS);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
