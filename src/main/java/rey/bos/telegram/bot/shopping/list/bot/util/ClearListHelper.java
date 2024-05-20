package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

@Component
@RequiredArgsConstructor
public class ClearListHelper {

    private final BotUtil botUtil;

    public EditMessageText buildMessage(UserDto user, int messageId, DictionaryKey key) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), key))
            .build();
    }

}
