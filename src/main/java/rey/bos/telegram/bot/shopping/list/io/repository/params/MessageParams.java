package rey.bos.telegram.bot.shopping.list.io.repository.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

@Getter
@Setter
@Builder
public class MessageParams {

    private long telegramId;
    private int messageId;
    private LanguageCode languageCode;

}
