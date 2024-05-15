package rey.bos.telegram.bot.shopping.list.bot;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.ShoppingListItemFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.BOT_COMMAND;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_SHOW_LIST;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
public class ShowListTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListBot shoppingListBot;
    @Autowired
    private MessageShoppingListService messageShoppingListService;
    @Autowired
    private ShoppingListService shoppingListService;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private ShoppingListItemFactory shoppingListItemFactory;

    @Test
    public void whenShowListCommandExecuteThenSaveMessageId() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        shoppingListItemFactory.addItem(shoppingList);

        Update update = createUpdateObjectWithCommand(user, MENU_COMMAND_SHOW_LIST.getCommand());
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        Optional<MessageShoppingList> storedMessage = messageShoppingListService.findMessageByUserAndListAndId(
            user.getId(), shoppingList.getId(), messageId
        );
        assertThat(storedMessage).isNotEmpty();
    }

    @Test
    public void whenShowListCommandExecuteTwiceThenSaveLastMessageId() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        ShoppingList shoppingList = shoppingListService.findActiveList(user.getId());
        shoppingListItemFactory.addItem(shoppingList);

        Update update = createUpdateObjectWithCommand(user, MENU_COMMAND_SHOW_LIST.getCommand());
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        int messageId2 = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId2).build());
        shoppingListBot.consume(update);

        Optional<MessageShoppingList> storedMessage = messageShoppingListService.findMessageByUserAndListAndId(
            user.getId(), shoppingList.getId(), messageId
        );
        assertThat(storedMessage).isEmpty();
        Optional<MessageShoppingList> storedMessage2 = messageShoppingListService.findMessageByUserAndListAndId(
            user.getId(), shoppingList.getId(), messageId2
        );
        assertThat(storedMessage2).isNotEmpty();
    }

    private Update createUpdateObjectWithCommand(UserDto userDto, String command) {
        Update update = new Update();
        Message message = new Message();
        message.setText(command);
        message.setEntities(List.of(
            MessageEntity.builder()
                .type(BOT_COMMAND.getDescription())
                .offset(0)
                .length(command.length())
                .text(command)
                .build()
        ));
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}
