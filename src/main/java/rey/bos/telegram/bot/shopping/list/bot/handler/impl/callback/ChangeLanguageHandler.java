package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CHANGE_LANGUAGE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.CHANGE_LANGUAGE_SUCCESS;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeLanguageHandler extends BotHandler {

    private final CallBackCommand command = CHANGE_LANGUAGE;

    private final UserService userService;
    private final BotUtil botUtil;
    private final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        String data = query.getData();
        String languageStr = data.replaceFirst(command.getCommand(), "");
        logCall(user.getId(), command.getCommand(), languageStr);
        LanguageCode language = LanguageCode.valueOf(languageStr);
        user = userService.updateUserLanguage(user.getId(), language);
        int messageId = query.getMessage().getMessageId();
        EditMessageText message = messageUtil.buildEditMessageText(user, messageId, CHANGE_LANGUAGE_SUCCESS);
        botUtil.executeMethod(message);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
