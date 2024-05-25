package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CONFIRM;

@AllArgsConstructor
public abstract class BotHandlerDecision extends BotHandler {

    public final CallBackCommand command;
    public final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        String data = query.getData();
        int messageId = query.getMessage().getMessageId();
        long callbackId = messageUtil.getIdByText(data, command.getCommand());
        logCall(user.getId(), command.getCommand(), data);

        return data.endsWith(CONFIRM.getCommand())
            ? handleAccept(user, messageId, callbackId)
            : handleReject(user, messageId, callbackId);
    }

    public abstract boolean handleAccept(User user, int messageId, long callbackId);

    public abstract boolean handleReject(User user, int messageId, long callbackId);

}
