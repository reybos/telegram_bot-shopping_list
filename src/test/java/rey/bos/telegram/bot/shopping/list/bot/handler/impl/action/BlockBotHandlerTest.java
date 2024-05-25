package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserShoppingListFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.BOT_COMMAND;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class BlockBotHandlerTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserFactory userFactory;
    @Autowired
    private BlockBotHandler blockBotHandler;
    @Autowired
    private UserService userService;
    @Autowired
    private ShoppingListBot shoppingListBot;
    @Autowired
    private UserShoppingListService userShoppingListService;
    @Autowired
    private UserShoppingListFactory userShoppingListFactory;

    @Test
    public void whenBlockBotThenBlockUser() {
        User user = userFactory.createUser();
        blockBotHandler.handle(new Update(), user);
        Optional<User> userO = userService.findActiveUserById(user.getId());
        assertThat(userO).isEmpty();
        user = userService.findByIdOrThrow(user.getId());
        assertThat(user.isBlocked()).isTrue();
    }

    @Test
    public void whenBlockBotAndRestartThenUnblockUser() {
        User user = userFactory.createUser();
        blockBotHandler.handle(new Update(), user);
        user = userService.findByIdOrThrow(user.getId());
        assertThat(user.isBlocked()).isTrue();
        Update update = createUpdateObjectWithStartCommand(user);
        shoppingListBot.consume(update);
        user = userService.findByIdOrThrow(user.getId());
        assertThat(user.isBlocked()).isFalse();
    }

    @Test
    public void whenBlockBotAndOwnGroupThenDisbandGroup() {
        User owner = userFactory.createUser();
        User user = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);
        UserShoppingList activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isFalse();
        blockBotHandler.handle(new Update(), owner);
        activeList = userShoppingListService.findActiveUserShoppingList(user.getId());
        assertThat(activeList.isOwner()).isTrue();
    }

    @Test
    public void whenBlockBotAndMemberGroupThenLeaveGroup() {
        User owner = userFactory.createUser();
        User user = userFactory.createUser();
        userShoppingListFactory.joinUsersList(user, owner);
        List<UserShoppingList> lists = userShoppingListService.findActiveGroupByUserId(owner.getId());
        assertThat(lists.size()).isEqualTo(2);
        blockBotHandler.handle(new Update(), user);
        lists = userShoppingListService.findActiveGroupByUserId(owner.getId());
        assertThat(lists.size()).isEqualTo(1);
    }

    private Update createUpdateObjectWithStartCommand(User storedUser) {
        Update update = new Update();
        Message message = new Message();
        String command = MenuCommand.MENU_COMMAND_START.getCommand();
        message.setText(command);
        message.setEntities(List.of(
            MessageEntity.builder()
                .type(BOT_COMMAND.getDescription())
                .offset(0)
                .length(command.length())
                .text(command)
                .build()
        ));
        org.telegram.telegrambots.meta.api.objects.User user = new org.telegram.telegrambots.meta.api.objects.User(
            storedUser.getTelegramId(), storedUser.getFirstName(), false
        );
        user.setUserName(storedUser.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}