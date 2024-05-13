package rey.bos.telegram.bot.shopping.list.bot;

import org.apache.commons.lang3.RandomStringUtils;
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
import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static rey.bos.telegram.bot.shopping.list.bot.MenuCommand.MENU_COMMAND_SHOW_LIST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler.BOT_COMMAND_TYPE;

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
    private UserService userService;

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private TelegramClient telegramClient;

    @Test
    public void whenShowListCommandExecuteThenSaveMessageId() throws TelegramApiException {
        long telegramId = new Random().nextLong();
        String itemValue = RandomStringUtils.randomAlphanumeric(10);
        Update update = createUpdateObjectWithText(telegramId, itemValue);
        shoppingListBot.consume(update);
        update = createUpdateObjectWithTextAndCommand(
            telegramId, MENU_COMMAND_SHOW_LIST.getCommand(), MENU_COMMAND_SHOW_LIST.getCommand()
        );
        int messageId = new Random().nextInt();
        when(telegramClient.execute(any(SendMessage.class))).thenReturn(Message.builder().messageId(messageId).build());
        shoppingListBot.consume(update);

        UserDto storedUser = userService.getOrCreateUser(UserDto.builder().telegramId(telegramId).build());
        ShoppingList shoppingList = shoppingListService.findActiveList(storedUser.getId());
        Optional<MessageShoppingList> storedMessage = messageShoppingListService.findMessageByChatAndListAndId(
            storedUser.getId(), shoppingList.getId(), messageId
        );
        assertThat(storedMessage).isNotEmpty();
    }

    private Update createUpdateObjectWithText(long telegramId, String text) {
        return createUpdateObjectWithTextAndCommand(
            telegramId, text, null
        );
    }

    private Update createUpdateObjectWithTextAndCommand(long telegramId, String text, String command) {
        Update update = new Update();
        Message message = new Message();
        message.setText(text);
        if (command != null) {
            message.setEntities(List.of(
                MessageEntity.builder()
                    .type(BOT_COMMAND_TYPE)
                    .offset(0)
                    .length(command.length())
                    .text(command)
                    .build()
            ));
        }
        User user = new User(telegramId, RandomStringUtils.randomAlphanumeric(10), false);
        user.setUserName(RandomStringUtils.randomAlphanumeric(10));
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

}
