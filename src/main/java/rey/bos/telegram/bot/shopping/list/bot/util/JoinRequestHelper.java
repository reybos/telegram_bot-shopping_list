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

    public SendMessage buildHasRequestMessage(UserDto user, List<JoinRequestParams> requestParams) {
        String users = requestParams.stream()
            .map(JoinRequestParams::getUserName)
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
        String groupUsers = group.stream()
            .map(UserShoppingListGroupParams::getUserName)
            .filter(name -> !name.equals(user.getUserName()))
            .map(name -> "@" + name)
            .collect(Collectors.joining(", "));
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
        String currentGroupOwner = group.stream()
            .filter(UserShoppingListGroupParams::isOwner)
            .map(item -> "@" + item.getUserName())
            .collect(Collectors.joining());
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

}
