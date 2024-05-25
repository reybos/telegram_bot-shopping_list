package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.GroupHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REMOVE_USER_FROM_GROUP_CONFIRM;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveUserFromGroupConfirmHandler extends BotHandler {

    private final CallBackCommand command = REMOVE_USER_FROM_GROUP_CONFIRM;

    private final MessageUtil messageUtil;
    private final BotUtil botUtil;
    private final UserShoppingListService userShoppingListService;
    private final GroupHelper groupHelper;

    @Override
    public boolean handle(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        String data = query.getData();
        int messageId = query.getMessage().getMessageId();
        long userListId = messageUtil.getIdByText(data, command.getCommand());
        UserShoppingListGroupParams userShoppingList = userShoppingListService.getUserListParamsById(userListId);
        EditMessageText messageText = groupHelper.buildRemoveUserFromGroup(user, messageId, userShoppingList);
        botUtil.executeMethod(messageText);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
