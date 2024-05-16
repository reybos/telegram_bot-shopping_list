package rey.bos.telegram.bot.shopping.list.bot;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
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
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.factory.JoinRequestFactory;
import rey.bos.telegram.bot.shopping.list.factory.UserFactory;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.ERROR_HAS_JOIN_REQUEST;
import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.USER_NOT_EXIST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.BOT_COMMAND;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.MENTION;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_JOIN_USER;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
public class JoinRequestProblemsTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private ShoppingListBot shoppingListBot;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private BotUtil botUtil;
    @Autowired
    private JoinRequestFactory joinRequestFactory;

    @ParameterizedTest
    @CsvSource({",,ERROR_EMPTY_MENTION_IN_JOIN", "@test,@test2,ERROR_TOO_MANY_MENTION_IN_JOIN"})
    public void whenJoinWithNotCorrectMentionThenError(
        String mention1, String mention2, DictionaryKey key
    ) throws TelegramApiException {
        UserDto user = userFactory.createUser();
        Update update = createUpdateObjectWithJoinCommand(user, mention1, mention2);
        shoppingListBot.consume(update);
        SendMessage message = getSendMessage();
        assertThat(message.getText()).isEqualTo(botUtil.getText(user.getLanguageCode(), key));
    }

    private SendMessage getSendMessage() throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

    @Test
    public void whenHasActiveRequestThenError() throws TelegramApiException {
        UserDto from = userFactory.createUser();
        UserDto to = userFactory.createUser();
        joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(from.getId())
                .ownerId(to.getId())
                .build()
        );
        Update update = createUpdateObjectWithJoinCommand(from, "@mention", null);
        shoppingListBot.consume(update);
        SendMessage message = getSendMessage();
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(from.getLanguageCode(), ERROR_HAS_JOIN_REQUEST).formatted("@" + to.getUserName())
        );
    }

    @Test
    public void whenMentionUnknownUserThenError() throws TelegramApiException {
        UserDto user = userFactory.createUser();
        String mention = "@test";
        Update update = createUpdateObjectWithJoinCommand(user, mention, null);
        shoppingListBot.consume(update);
        SendMessage message = getSendMessage();
        assertThat(message.getText()).isEqualTo(
            botUtil.getText(user.getLanguageCode(), USER_NOT_EXIST).formatted(mention)
        );
    }

    @Test
    public void whenHasOwnActiveGroupThenError() {
        //todo
    }

    @Test
    public void whenMemberGroupThenError() {
        //todo
    }

    private Update createUpdateObjectWithJoinCommand(UserDto userDto, String mention1, String mention2) {
        Update update = new Update();
        Message message = new Message();
        String text = MENU_COMMAND_JOIN_USER.getCommand() +
            (mention1 == null ? "" : " " + mention1) +
            (mention2 == null ? "" : " " + mention2);
        message.setText(text);
        message.setEntities(buildEntities(mention1, mention2));
        User user = new User(userDto.getTelegramId(), userDto.getFirstName(), false);
        user.setUserName(userDto.getUserName());
        message.setFrom(user);
        update.setMessage(message);
        return update;
    }

    private List<MessageEntity> buildEntities(String mention1, String mention2) {
        List<MessageEntity> entities = new ArrayList<>();
        int offset = 0;
        entities.add(
            MessageEntity.builder()
                .type(BOT_COMMAND.getDescription())
                .offset(offset)
                .length(MENU_COMMAND_JOIN_USER.getCommand().length())
                .text(MENU_COMMAND_JOIN_USER.getCommand())
                .build()
        );
        offset += MENU_COMMAND_JOIN_USER.getCommand().length() + 1;
        if (mention1 != null) {
            entities.add(buildMentionEntity(mention1, offset));
            offset += mention1.length() + 1;
        }
        if (mention2 != null) {
            entities.add(buildMentionEntity(mention2, offset));
        }
        return entities;
    }

    private MessageEntity buildMentionEntity(String mention, int offset) {
        return MessageEntity.builder()
            .type(MENTION.getDescription())
            .offset(offset)
            .length(mention.length())
            .text(mention)
            .build();
    }

}
