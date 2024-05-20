package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@Component
@RequiredArgsConstructor
public class GroupHelper {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    public SendMessage buildGroupMessage(UserDto user, List<UserShoppingListGroupParams> group) {
        UserShoppingListGroupParams owner = group.stream().filter(UserShoppingListGroupParams::isOwner).toList().get(0);
        group = group.stream().filter(item -> item.getUserId() != user.getId()).toList();
        if (CollectionUtils.isEmpty(group)) {
            return buildEmptyGroup(user);
        } else if (owner.getUserId() == user.getId()) {
            return buildOwnerGroup(user, group);
        } else {
            return buildMemberGroup(user, owner);
        }
    }

    private SendMessage buildEmptyGroup(UserDto user) {
        return SendMessage
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), EMPTY_GROUP_MESSAGE))
            .build();
    }

    private SendMessage buildOwnerGroup(UserDto user, List<UserShoppingListGroupParams> group) {
        return SendMessage
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), OWNER_GROUP_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildItems(group))
                .build())
            .build();
    }

    private List<InlineKeyboardRow> buildItems(List<UserShoppingListGroupParams> group) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        for (UserShoppingListGroupParams params : group) {
            rows.add(new InlineKeyboardRow(
                InlineKeyboardButton
                    .builder()
                    .text(messageUtil.getLogin(params.getUserName()))
                    .callbackData(REMOVE_USER_FROM_GROUP_CONFIRM.getCommand() + params.getUserListId())
                    .build()
            ));
        }
        return rows;
    }

    private SendMessage buildMemberGroup(UserDto user, UserShoppingListGroupParams owner) {
        return SendMessage
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), MEMBER_GROUP_MESSAGE)
                .formatted(messageUtil.getLogin(owner.getUserName())))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(new InlineKeyboardRow(
                    InlineKeyboardButton
                        .builder()
                        .text(botUtil.getText(user.getLanguageCode(), LEAVE_GROUP_BUTTON))
                        .callbackData(LEAVE_GROUP_CONFIRM.getCommand())
                        .build()
                )))
                .build())
            .build();
    }

    public EditMessageText buildLeaveGroupConfirm(UserDto user, int messageId) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), LEAVE_GROUP_CONFIRM_MESSAGE))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, LEAVE_GROUP))
                .build())
            .build();
    }

    public EditMessageText buildLeftGroup(UserDto user, int messageId) {
        return EditMessageText
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), LEFT_GROUP_MESSAGE))
            .build();
    }

    public EditMessageText buildLeaveGroupCancel(UserDto user, int messageId) {
        return EditMessageText
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), LEAVE_GROUP_CANCEL_MESSAGE))
            .build();
    }

    public EditMessageText buildRemoveUserFromGroup(
        UserDto user, int messageId, UserShoppingListGroupParams userShoppingList
    ) {
        return EditMessageText
            .builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), REMOVE_USER_FROM_GROUP_CONFIRM_MESSAGE)
                .formatted(messageUtil.getLogin(userShoppingList.getUserName())))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(
                    userShoppingList.getUserListId(), user.getLanguageCode(), REMOVE_USER_FROM_GROUP)
                )
                .build())
            .build();
    }

    public EditMessageText buildRemoveGroupCancel(UserDto user, int messageId, String userName) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), REMOVE_FROM_GROUP_CANCEL_MESSAGE)
                .formatted(messageUtil.getLogin(userName)))
            .build();
    }

    public EditMessageText buildUserRemovedFromGroup(UserDto user, int messageId, String userName) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), USER_REMOVED_FROM_GROUP_MESSAGE)
                .formatted(messageUtil.getLogin(userName)))
            .build();
    }

}
