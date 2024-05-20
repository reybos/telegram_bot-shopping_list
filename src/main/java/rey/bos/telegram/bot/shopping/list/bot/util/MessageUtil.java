package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CONFIRM_MSG;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.REJECT_MSG;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.REJECT;

@Component
@AllArgsConstructor
@Slf4j
public class MessageUtil {

    private final BotUtil botUtil;

    public String getLoginsExcludingCurrentUser(List<UserShoppingListGroupParams> group, UserDto user) {
        return group.stream()
            .map(UserShoppingListGroupParams::getUserName)
            .filter(name -> !name.equals(user.getUserName()))
            .map(this::getLogin)
            .collect(Collectors.joining(", "));
    }

    public String getGroupOwnerLogin(List<UserShoppingListGroupParams> group) {
        return group.stream()
            .filter(UserShoppingListGroupParams::isOwner)
            .map(item -> getLogin(item.getUserName()))
            .collect(Collectors.joining());
    }

    public long getIdByText(String text, String command) {
        text = text.replaceFirst(command, "");
        text = text.replaceFirst(CallBackCommand.CONFIRM.getCommand(), "");
        text = text.replaceFirst(CallBackCommand.REJECT.getCommand(), "");
        return !text.isEmpty() ? Long.parseLong(text) : -1;
    }

    public String getLogin(String userName) {
        return "@" + userName;
    }

    public List<InlineKeyboardRow> buildYesNoButtons(UserDto user, CallBackCommand command) {
        return buildYesNoButtons(user.getId(), user.getLanguageCode(), command);
    }

    public List<InlineKeyboardRow> buildYesNoButtons(long id, LanguageCode code, CallBackCommand command) {
        return List.of(
            new InlineKeyboardRow(
                InlineKeyboardButton
                    .builder()
                    .text(botUtil.getText(code, CONFIRM_MSG))
                    .callbackData(command.getCommand() + id + CONFIRM.getCommand())
                    .build(),
                InlineKeyboardButton
                    .builder()
                    .text(botUtil.getText(code, REJECT_MSG))
                    .callbackData(command.getCommand() + id + REJECT.getCommand())
                    .build()
            )
        );
    }

}
