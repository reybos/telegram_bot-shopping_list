package rey.bos.telegram.bot.shopping.list.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.handler.ChatMemberStatus;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockBotHandler extends BotHandler {

    //todo https://github.com/reybos/telegram-bot-shopping-list/issues/19
    @Override
    public boolean handle(Update update, UserDto user) {
        log.info("User with telegramId = " + user.getTelegramId() + " block bot");
        return true;
    }

    @Override
    public boolean support(Update update) {
        return update.hasMyChatMember()
            && update.getMyChatMember().getNewChatMember().getStatus().equals(ChatMemberStatus.KICKED.getValue());
    }

}
