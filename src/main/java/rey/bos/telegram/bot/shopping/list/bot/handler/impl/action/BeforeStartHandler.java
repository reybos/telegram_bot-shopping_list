package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.handler.ChatMemberStatus;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

@Component
@Slf4j
public class BeforeStartHandler extends BotHandler {

    @Override
    public boolean handle(Update update, UserDto user) {
        log.info("Received a request before the /start command for the user with telegramId = " + user.getTelegramId());
        return true;
    }

    @Override
    public boolean support(Update update) {
        return update.hasMyChatMember()
            && update.getMyChatMember().getNewChatMember().getStatus().equals(ChatMemberStatus.MEMBER.getValue());
    }

}
