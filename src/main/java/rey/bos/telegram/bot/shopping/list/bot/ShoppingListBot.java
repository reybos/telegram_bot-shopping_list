package rey.bos.telegram.bot.shopping.list.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.shared.mapper.UserDtoMapper;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.ERROR_OR_UNHANDLED_COMMAND;

@Component
@Slf4j
@Setter
public class ShoppingListBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final List<BotHandler> handlers;
    private final BotUtil botUtil;
    private final UserDtoMapper userDtoMapper;
    private final UserService userService;


    @Value("${telegram.token}")
    private String botToken;

    public ShoppingListBot(
        TelegramClient telegramClient, List<BotHandler> handlers, BotUtil botUtil,
        UserDtoMapper userDtoMapper, UserService userService
    ) {
        this.telegramClient = telegramClient;
        this.handlers = handlers;
        this.botUtil = botUtil;
        this.userDtoMapper = userDtoMapper;
        this.userService = userService;
//        setCommands();
    }

    private void setCommands() {
        SetMyCommands setMyCommands = new SetMyCommands(List.of(
            new BotCommand("/list", "view the list"),
            new BotCommand("/clear_list", "clear the list"),
            new BotCommand("/help", "instruction")
        ));
        try {
            telegramClient.execute(setMyCommands); // Sending our message object to user
        } catch (TelegramApiException e) {
            log.error("Can't execute command", e);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
//        log.info(update.toString());
        UserDto user = getOrCreateUser(update);
        boolean handled = false;
        for (BotHandler handler : handlers) {
            if (handler.support(update)) {
                handled = handler.handle(update, user);
            }
        }
        if (!handled) {
            botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), ERROR_OR_UNHANDLED_COMMAND);
        }
    }

    public UserDto getOrCreateUser(Update update) {
        User user = update.hasMyChatMember() ? update.getMyChatMember().getFrom() : update.getMessage().getFrom();
        UserDto userDto = userDtoMapper.map(user);
        return userService.getOrCreateUser(userDto);
    }


    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: " + botSession.isRunning());
    }

}