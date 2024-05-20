package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.ERROR_HAS_JOIN_REQUEST;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.SEND_JOIN_REQUEST_SUCCESS;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;

@Component
@RequiredArgsConstructor
public class JoinRequestHelper {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    public SendMessage buildHasRequestMessage(UserDto user, List<JoinRequestParams> requestParams) {
        String users = requestParams.stream()
            .map(JoinRequestParams::getOwnerUserName)
            .collect(Collectors.joining(", "));
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), ERROR_HAS_JOIN_REQUEST).formatted(users))
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, CLEAR_EXIST_JOIN_REQUEST))
                .build())
            .build();
    }

    public SendMessage buildHasActiveGroupMessage(
        List<UserShoppingListGroupParams> group, UserDto user, String mentionUser
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, user);
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.ERROR_SENDER_IS_OWNER_ACTIVE_GROUP)
            .formatted(groupUsers, mentionUser, groupUsers);

        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, DISBAND_GROUP_AND_JOIN))
                .build())
            .build();
    }

    public SendMessage buildLeaveGroupMessage(
        List<UserShoppingListGroupParams> group, UserDto user, String mentionUser
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.ERROR_SENDER_IS_MEMBER_OF_GROUP)
            .formatted(currentGroupOwner, mentionUser);

        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, LEAVE_GROUP_AND_JOIN))
                .build())
            .build();
    }

    public SendMessage buildAcceptJoinRequestWithoutActiveGroup(UserDto currUser, UserDto mentionUser) {
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()));

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithOwnActiveGroup(
        UserDto currUser, UserDto mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, mentionUser);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITH_OWN_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()), groupUsers);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithActiveGroup(
        UserDto currUser, UserDto mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITH_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()), currentGroupOwner);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequest(UserDto user, String text) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, ACCEPT_JOIN_REQUEST))
                .build())
            .build();
    }

    public SendMessage buildSendJoinRequestSuccess(UserDto user, String mentionUserName) {
        String text = botUtil.getText(user.getLanguageCode(), SEND_JOIN_REQUEST_SUCCESS)
            .formatted(mentionUserName);
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .build();
    }

}
