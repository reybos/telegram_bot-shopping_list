package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.Dictionary;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BotUtil {

    private final TelegramClient telegramClient;
    private final List<Dictionary> dictionaries;

    public void sendMessageByKey(Long chatId, LanguageCode languageCode, DictionaryKey key) {
        String message = getText(languageCode, key);
        sendMessage(chatId, message);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage // Create a message object
            .builder()
            .parseMode("HTML")
            .chatId(chatId)
            .text(text)
            .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            log.error("Can't execute command", e);
        }
    }

    public String getText(LanguageCode languageCode, DictionaryKey key) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.isSuitable(languageCode)) {
                return dictionary.get(key);
            }
        }
        return "";
    }

}
