package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CLEAR_JOIN_REQUEST;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearJoinRequestHandler extends BotHandler {

    //TODO
    @Override
    public boolean handle(Update update, UserDto user) {
        CallbackQuery query = update.getCallbackQuery();
        String command = query.getData();
        int messageId = query.getMessage().getMessageId();
        if (command.endsWith(CONFIRM.getCommand())) {
            handleAccept(user, messageId);
        } else {
            handleReject(user, messageId);
        }
        return true;
    }

    private void handleAccept(UserDto user, int messageId) {

    }

    private void handleReject(UserDto user, int messageId) {

    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, CLEAR_JOIN_REQUEST);
    }

}
