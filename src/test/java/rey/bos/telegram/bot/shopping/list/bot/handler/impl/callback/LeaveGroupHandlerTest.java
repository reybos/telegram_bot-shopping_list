package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_GROUP;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class LeaveGroupHandlerTest {

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private UserShoppingListFactory userShoppingListFactory;
    @Autowired
    private UserShoppingListService userShoppingListService;
    @Autowired
    private LeaveGroupHandler leaveGroupHandler;

    @Test
    public void whenLeaveGroupConfirmThenSuccess() {
        UserDto user = userFactory.createUser();
        UserDto owner = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);
        UserShoppingList activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isFalse();

        Update update = createUpdateObjectWithCallback(user, activeList.getId(), CONFIRM.getCommand());
        leaveGroupHandler.handle(update, user);

        activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isTrue();
        UserShoppingList ownerList = userShoppingListService.findActiveUserShoppingList(owner.getId());
        assertThat(ownerList.isOwner()).isTrue();

        List<UserShoppingList> group = userShoppingListService.findActiveGroupByUserId(user.getId());
        assertThat(group).noneMatch(item -> item.getUserId() == owner.getId());
    }

    private Update createUpdateObjectWithCallback(UserDto userDto, long id, String decision) {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(LEAVE_GROUP.getCommand() + id + decision);
        Message message = new Message();
        message.setMessageId(new Random().nextInt());
        callbackQuery.setMessage(message);
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        callbackQuery.setFrom(user);
        update.setCallbackQuery(callbackQuery);
        return update;
    }

}