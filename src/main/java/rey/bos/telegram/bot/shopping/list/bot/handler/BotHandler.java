package rey.bos.telegram.bot.shopping.list.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

public abstract class BotHandler {

    public static final String BOT_COMMAND_TYPE = "bot_command";

    public abstract boolean handle(Update update, UserDto user);

    public abstract boolean support(Update update);

}
