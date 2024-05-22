package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;

@AllArgsConstructor
public abstract class BotHandlerDecision extends BotHandler {

    public final CallBackCommand command;
    public final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        CallbackQuery query = update.getCallbackQuery();
        String data = query.getData();
        int messageId = query.getMessage().getMessageId();
        long callbackId = messageUtil.getIdByText(data, command.getCommand());

        return data.endsWith(CONFIRM.getCommand())
            ? handleAccept(user, messageId, callbackId)
            : handleReject(user, messageId, callbackId);
    }

    public abstract boolean handleAccept(UserDto user, int messageId, long callbackId);

    public abstract boolean handleReject(UserDto user, int messageId, long callbackId);

}
