package rey.bos.telegram.bot.shopping.list.bot.helper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.List;
import java.util.stream.Collectors;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.*;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;
import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.EN;
import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.RU;

@Component
@RequiredArgsConstructor
public class JoinRequestHelper {

    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    public SendMessage buildHasRequestMessage(User user, List<JoinRequestParams> requestParams) {
        String users = requestParams.stream()
            .map(JoinRequestParams::getOwnerUserName)
            .collect(Collectors.joining(", "));
        return messageUtil.buildSendMessageWithButtons(
            user, ERROR_HAS_ACTIVE_JOIN_REQUEST, messageUtil.buildYesNoButtons(user, CLEAR_SENT_JOIN_REQUEST), users
        );
    }

    public SendMessage buildHasActiveGroupMessage(
        List<UserShoppingListGroupParams> group, User user, String mentionUser
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, user);
        return messageUtil.buildSendMessageWithButtons(
            user, ERROR_SENDER_IS_OWNER_ACTIVE_GROUP, messageUtil.buildYesNoButtons(user, DISBAND_GROUP_BEFORE_JOIN),
            groupUsers, mentionUser, groupUsers
        );
    }

    public SendMessage buildLeaveGroupMessage(
        List<UserShoppingListGroupParams> group, User user, String mentionUser
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        return messageUtil.buildSendMessageWithButtons(
            user, ERROR_SENDER_IS_MEMBER_OF_GROUP, messageUtil.buildYesNoButtons(user, LEAVE_GROUP_BEFORE_JOIN),
            currentGroupOwner, mentionUser
        );
    }

    public SendMessage buildAcceptJoinRequestWithoutActiveGroup(User currUser, User mentionUser) {
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()));

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithOwnActiveGroup(
        User currUser, User mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String groupUsers = messageUtil.getLoginsExcludingCurrentUser(group, mentionUser);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITH_OWN_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()), groupUsers);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequestWithActiveGroup(
        User currUser, User mentionUser, List<UserShoppingListGroupParams> group
    ) {
        String currentGroupOwner = messageUtil.getGroupOwnerLogin(group);
        String text = botUtil.getText(
            mentionUser.getLanguageCode(), DictionaryKey.OWNER_ACCEPT_JOIN_REQUEST_WITH_ACTIVE_GROUP
        ).formatted(messageUtil.getLogin(currUser.getUserName()), currentGroupOwner);

        return buildAcceptJoinRequest(mentionUser, text);
    }

    public SendMessage buildAcceptJoinRequest(User user, String text) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(text)
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(messageUtil.buildYesNoButtons(user, ACCEPT_JOIN_REQUEST))
                .build())
            .build();
    }

    public String getUserWithoutLoginMessage(User user) {
        boolean isNumericLogin = StringUtils.isNumeric(user.getUserName());
        if (user.getLanguageCode() == RU) {
            return isNumericLogin
                ? "<b>ВАЖНО!</b> Так как у вас нет логина в телеграмм, чтобы прислать вам запрос на объединение, " +
                "дайте другому пользователю этот логин: @" + user.getUserName() + " и пусть он пришлет его боту"
                : "";
        } else if (user.getLanguageCode() == EN) {
            return isNumericLogin
                ? "<b>IMPORTANT!</b> Since you do not have a telegram login to send you a merge request, give another " +
                "user this login: @" + user.getUserName() + " and let him send it to the bot"
                : "";
        }
        return "";
    }

}
