package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class MessageUtil {

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
        return Long.parseLong(text);
    }

    public String getLogin(String userName) {
        return "@" + userName;
    }

}
