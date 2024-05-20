package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.GroupHelper;
import rey.bos.telegram.bot.shopping.list.bot.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.YOU_REMOVED_FROM_GROUP_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REMOVE_USER_FROM_GROUP;

@Slf4j
@Component
public class RemoveUserFromGroupHandler extends BotHandlerDecision {

    private final UserShoppingListService userShoppingListService;
    private final BotUtil botUtil;
    private final GroupHelper groupHelper;
    private final UserService userService;

    public RemoveUserFromGroupHandler(
        MessageUtil messageUtil, UserShoppingListService userShoppingListService, BotUtil botUtil,
        GroupHelper groupHelper, UserService userService
    ) {
        super(REMOVE_USER_FROM_GROUP, messageUtil);
        this.userShoppingListService = userShoppingListService;
        this.botUtil = botUtil;
        this.groupHelper = groupHelper;
        this.userService = userService;
    }

    @Override
    public boolean handleAccept(UserDto user, int messageId, long callbackId) {
        UserShoppingListGroupParams params = userShoppingListService.getUserListParamsById(callbackId);
        UserDto removedUser = userService.findByIdOrThrow(params.getUserId());
        userShoppingListService.restoreMainList(removedUser.getId());
        EditMessageText message = groupHelper.buildUserRemovedFromGroup(user, messageId, removedUser.getUserName());
        botUtil.executeMethod(message);
        String removedUserMessage = botUtil.getText(removedUser.getLanguageCode(), YOU_REMOVED_FROM_GROUP_MESSAGE)
            .formatted(messageUtil.getLogin(user.getUserName()));
        botUtil.sendMessage(removedUser.getTelegramId(), removedUserMessage);
        return true;
    }

    @Override
    public boolean handleReject(UserDto user, int messageId, long callbackId) {
        UserShoppingListGroupParams params = userShoppingListService.getUserListParamsById(callbackId);
        EditMessageText message = groupHelper.buildRemoveGroupCancel(user, messageId, params.getUserName());
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}