package rey.bos.telegram.bot.shopping.list.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

public interface BotHandler {

    boolean handle(Update update, UserDto user);

    boolean support(Update update);

}
