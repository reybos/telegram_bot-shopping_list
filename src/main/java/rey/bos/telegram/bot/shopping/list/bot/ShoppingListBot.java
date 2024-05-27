package rey.bos.telegram.bot.shopping.list.bot;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand;
import rey.bos.telegram.bot.shopping.list.config.ShoppingListBotProperty;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.shared.mapper.UserDtoMapper;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Setter
public class ShoppingListBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final List<BotHandler> handlers;
    private final BotUtil botUtil;
    private final UserDtoMapper userDtoMapper;
    private final UserService userService;
    private final ShoppingListBotProperty property;

    public ShoppingListBot(
        TelegramClient telegramClient, List<BotHandler> handlers, BotUtil botUtil,
        UserDtoMapper userDtoMapper, UserService userService, ShoppingListBotProperty property
    ) {
        this.telegramClient = telegramClient;
        this.handlers = handlers;
        this.botUtil = botUtil;
        this.userDtoMapper = userDtoMapper;
        this.userService = userService;
        this.property = property;
        setCommands();
    }

    private void setCommands() {
        List<BotCommand> commands = new ArrayList<>();
        for (MenuCommand menuCommand : MenuCommand.getCommandsForMenu()) {
            commands.add(new BotCommand(menuCommand.getCommand(), menuCommand.getDescription()));
        }
        SetMyCommands setMyCommands = new SetMyCommands(commands);
        try {
            telegramClient.execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Can't execute command", e);
        }
    }

    @Override
    public String getBotToken() {
        return property.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        User user = getOrCreateUser(update);
        boolean handled = false;
        for (BotHandler handler : handlers) {
            if (handler.support(update)) {
                handled = handler.handle(update, user);
            }
        }
        if (!handled) {
            botUtil.sendSomethingWentMessage(user.getTelegramId(), user.getLanguageCode());
        }
    }

    public User getOrCreateUser(Update update) {
        org.telegram.telegrambots.meta.api.objects.User user;
        if (update.hasMyChatMember()) {
            user = update.getMyChatMember().getFrom();
        } else if (update.hasCallbackQuery()) {
            user = update.getCallbackQuery().getFrom();
        } else {
            user = update.getMessage().getFrom();
        }
        UserDto userDto = userDtoMapper.map(user);
        return userService.getOrCreateUser(userDto);
    }


    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: " + botSession.isRunning());
    }

}