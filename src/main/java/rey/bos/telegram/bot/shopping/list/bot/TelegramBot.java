package rey.bos.telegram.bot.shopping.list.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@Slf4j
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public TelegramBot(@Value("${telegram.token}") String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        setCommands();
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
    public void consume(Update update) {
        log.info(update.getMessage().getFrom().toString());
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            switch (message_text) {
                case "/start" -> {
                    String greeting = """
                        The user's greeting!
                        Description of the bot's work.
                        """;
                    // User send /start
                    SendMessage message = SendMessage // Create a message object
                        .builder()
                        .parseMode("HTML")
                        .chatId(chat_id)
                        .text(greeting)
                        .build();
                    try {
                        telegramClient.execute(message); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        log.error("Can't execute command", e);
                    }
                }
                case "/list" -> {
                    SendMessage message = SendMessage.builder()
                        .parseMode("HTML")
                        .chatId(chat_id)
                        .text(
                            """
                            <b>Your list</b>
                            
                            Click on any item to delete
                            """
                        )
                        .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(List.of(
                                new InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text("mandrake root 3 pieces")
                                        .callbackData("update_msg_text")
                                        .build()
                                ),
                                new InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text("unicorn meat 500g")
                                        .callbackData("update_msg_text")
                                        .build()
                                ),
                                new InlineKeyboardRow(
                                    InlineKeyboardButton
                                        .builder()
                                        .text("123456789012345678901234567890")
                                        .callbackData("update_msg_text")
                                        .build()
                                )
                            ))
                            .build())
                        .build();
                    try {
                        telegramClient.execute(message); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        log.error("Can't execute command", e);
                    }
                }
                default -> {
                    // Unknown command
                    SendMessage message = SendMessage // Create a message object
                        .builder()
                        .chatId(chat_id)
                        .text("The item has been added to your list")
                        .build();
                    try {
                        telegramClient.execute(message); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        log.error("Can't execute command", e);
                    }
                }
            }
        }
    }

}