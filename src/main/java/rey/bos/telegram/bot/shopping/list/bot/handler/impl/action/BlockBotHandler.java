package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.handler.ChatMemberStatus;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.List;
import java.util.Optional;

import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockBotHandler extends BotHandler {

    private final UserService userService;
    private final BotUtil botUtil;
    private final MessageUtil messageUtil;
    private final UserShoppingListService userShoppingListService;

    @Override
    public boolean handle(Update update, UserDto user) {
        UserShoppingList userShoppingList = userShoppingListService.findActiveUserShoppingList(user.getId());
        userService.blockUser(user);
        if (userShoppingList.isOwner()) {
            List<Long> userIds = userShoppingListService.disbandGroup(user.getId());
            List<UserDto> removedUsers = userService.findActiveUsersByIds(userIds);
            String ownerLogin = messageUtil.getLogin(user.getUserName());
            for (UserDto removedUser : removedUsers) {
                SendMessage message = messageUtil.buildSendMessage(
                    removedUser, OWNER_BLOCKED_BOT_YOU_REMOVED_FROM_GROUP_MESSAGE, ownerLogin
                );
                botUtil.executeMethod(message);
            }
        } else {
            List<UserShoppingList> group = userShoppingListService.findActiveGroupByUserId(user.getId());
            UserShoppingList ownerList = group.stream().filter(UserShoppingList::isOwner).toList().get(0);
            userShoppingListService.restoreMainList(user.getId());
            Optional<UserDto> ownerO = userService.findActiveUserById(ownerList.getUserId());
            if (ownerO.isPresent()) {
                SendMessage sendMessage = messageUtil.buildSendMessage(
                    ownerO.get(), USER_BLOCKED_BOT_AND_LEFT_YOUR_GROUP_MESSAGE, messageUtil.getLogin(user.getUserName())
                );
                botUtil.executeMethod(sendMessage);
            }
        }
        return true;
    }

    @Override
    public boolean support(Update update) {
        return update.hasMyChatMember()
            && update.getMyChatMember().getNewChatMember().getStatus().equals(ChatMemberStatus.KICKED.getValue());
    }

}
