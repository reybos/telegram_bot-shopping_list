package rey.bos.telegram.bot.shopping.list.bot.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@Component
@RequiredArgsConstructor
public class GroupHelper {

    private final MessageUtil messageUtil;

    public SendMessage buildGroupMessage(UserDto user, List<UserShoppingListGroupParams> group) {
        UserShoppingListGroupParams owner = group.stream().filter(UserShoppingListGroupParams::isOwner).toList().get(0);
        group = group.stream().filter(item -> item.getUserId() != user.getId()).toList();
        if (CollectionUtils.isEmpty(group)) {
            return messageUtil.buildSendMessage(user, EMPTY_GROUP_MESSAGE);
        } else if (owner.getUserId() == user.getId()) {
            return buildOwnerGroup(user, group);
        } else {
            return buildMemberGroup(user, owner);
        }
    }

    private SendMessage buildOwnerGroup(UserDto user, List<UserShoppingListGroupParams> group) {
        return messageUtil.buildSendMessageWithButtons(user, OWNER_GROUP_MESSAGE, buildItems(group));
    }

    private List<InlineKeyboardRow> buildItems(List<UserShoppingListGroupParams> group) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (UserShoppingListGroupParams params : group) {
            rows.add(new InlineKeyboardRow(
                messageUtil.buildButton(
                    messageUtil.getLogin(params.getUserName()),
                    REMOVE_USER_FROM_GROUP_CONFIRM.getCommand() + params.getUserListId()
                )
            ));
        }
        return rows;
    }

    private SendMessage buildMemberGroup(UserDto user, UserShoppingListGroupParams owner) {
        List<InlineKeyboardRow> buttons = List.of(new InlineKeyboardRow(
            messageUtil.buildButton(
                user.getLanguageCode(), LEAVE_GROUP_BUTTON, LEAVE_GROUP_CONFIRM.getCommand()
            )
        ));
        return messageUtil.buildSendMessageWithButtons(
            user, MEMBER_GROUP_MESSAGE, buttons, messageUtil.getLogin(owner.getUserName())
        );
    }

    public EditMessageText buildRemoveUserFromGroup(
        UserDto user, int messageId, UserShoppingListGroupParams userShoppingList
    ) {
        return messageUtil.buildEditMessageTextWithButtons(
            user, messageId, REMOVE_USER_FROM_GROUP_CONFIRM_MESSAGE,
            messageUtil.buildYesNoButtons(
                userShoppingList.getUserListId(), user.getLanguageCode(), REMOVE_USER_FROM_GROUP
            ),
            messageUtil.getLogin(userShoppingList.getUserName())
        );
    }

}
