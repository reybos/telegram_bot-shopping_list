package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.LEAVE_CURRENT_GROUP;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveCurrentGroupHandler extends BotHandler {

    @Override
    public boolean handle(Update update, UserDto user) {
        String command = update.getCallbackQuery().getData();
        if (command.endsWith(CONFIRM.getCommand())) {
            log.info("confirm " + command);
        } else {
            log.info("reject " + command);
        }
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, LEAVE_CURRENT_GROUP);
    }

}