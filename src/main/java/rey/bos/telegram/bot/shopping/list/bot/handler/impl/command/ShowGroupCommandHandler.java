package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.GroupHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_GROUP;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowGroupCommandHandler extends BotHandler {

    private final GroupHelper groupHelper;
    private final UserShoppingListService userShoppingListService;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        UserShoppingList activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        List<UserShoppingListGroupParams> group = userShoppingListService.findActiveGroupByListId(
            activeList.getShoppingListId()
        );
        SendMessage message = groupHelper.buildGroupMessage(user, group);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportMenuCommand(update, MENU_COMMAND_GROUP);
    }

}
