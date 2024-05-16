package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;
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
                .keyboard(buildYesNoButtons(user, CLEAR_JOIN_REQUEST))
                .build())
            .build();
    }

    public SendMessage buildHasActiveGroupMessage(
        List<UserShoppingListGroupParams> group, UserDto user, String mentionUser
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, user);
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.ERROR_OWNER_HAS_ACTIVE_GROUP)
            .formatted(groupUsers, mentionUser, groupUsers);

        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildYesNoButtons(user, DISBAND_CURRENT_GROUP))
                .build())
            .build();
    }

    public SendMessage buildLeaveGroupMessage(
        List<UserShoppingListGroupParams> group, UserDto user, String mentionUser
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        String text = botUtil.getText(user.getLanguageCode(), DictionaryKey.ERROR_MEMBER_OF_GROUP)
            .formatted(currentGroupOwner, mentionUser);

        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildYesNoButtons(user, LEAVE_CURRENT_GROUP))
                .build())
            .build();
    }

    public List<InlineKeyboardRow> buildYesNoButtons(UserDto user, CallBackCommand command) {
        return List.of(
            new InlineKeyboardRow(
                InlineKeyboardButton
                    .builder()
                    .text(botUtil.getText(user.getLanguageCode(), CONFIRM_MSG))
                    .callbackData(command.getCommand() + user.getId() + CONFIRM.getCommand())
                    .build(),
                InlineKeyboardButton
                    .builder()
                    .text(botUtil.getText(user.getLanguageCode(), REJECT_MSG))
                    .callbackData(command.getCommand() + user.getId() + REJECT.getCommand())
                    .build()
            )
        );
    }

    public SendMessage buildAcceptJoinRequestWithoutActiveGroup(UserDto currUser, UserDto mentionUser) {
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP
        ).formatted("@" + currUser.getUserName());

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithOwnActiveGroup(
        UserDto currUser, UserDto mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, mentionUser);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.ACCEPT_JOIN_REQUEST_WITH_OWN_ACTIVE_GROUP
        ).formatted("@" + currUser.getUserName(), groupUsers);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithActiveGroup(
        UserDto currUser, UserDto mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.ACCEPT_JOIN_REQUEST_WITH_ACTIVE_GROUP
        ).formatted("@" + currUser.getUserName(), currentGroupOwner);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequest(UserDto user, String text) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(buildYesNoButtons(user, ACCEPT_JOIN_REQUEST))
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