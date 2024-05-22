package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.DISBAND_GROUP_BEFORE_JOIN;

@Slf4j
@Component
public class DisbandGroupBeforeJoinHandler extends BotHandlerDecision {

    private final BotUtil botUtil;
    private final UserShoppingListService userShoppingListService;
    private final UserService userService;

    public DisbandGroupBeforeJoinHandler(
        MessageUtil messageUtil, BotUtil botUtil, UserShoppingListService userShoppingListService,
        UserService userService
    ) {
        super(DISBAND_GROUP_BEFORE_JOIN, messageUtil);
        this.botUtil = botUtil;
        this.userShoppingListService = userShoppingListService;
        this.userService = userService;
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        UserShoppingList userShoppingList = userShoppingListService.findActiveUserShoppingList(user.getId());
        if (!userShoppingList.isOwner()) {
            EditMessageText messageText = messageUtil.buildEditMessageText(
                user, messageId, ERROR_MEMBER_CANT_DISBAND_GROUP
            );
            botUtil.executeMethod(messageText);
            return true;
        }
        List<Long> userIds = userShoppingListService.disbandGroup(user.getId());

        EditMessageText ownerMessage = messageUtil.buildEditMessageText(user, messageId, DISBAND_GROUP_SUCCESS_MESSAGE);
        botUtil.executeMethod(ownerMessage);

        List<UserDto> removedUsers = userService.findActiveUsersByIds(userIds);
        String ownerLogin = messageUtil.getLogin(user.getUserName());
        for (UserDto removedUser : removedUsers) {
            SendMessage message = messageUtil.buildSendMessage(removedUser, YOU_REMOVED_FROM_GROUP_MESSAGE, ownerLogin);
            botUtil.executeMethod(message);
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
