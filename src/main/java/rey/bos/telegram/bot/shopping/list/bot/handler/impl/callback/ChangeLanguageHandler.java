package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.CHANGE_LANGUAGE_SUCCESS;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback.CallBackCommand.CHANGE_LANGUAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeLanguageHandler extends BotHandler {

    private final CallBackCommand command = CHANGE_LANGUAGE;

    private final UserService userService;
    private final BotUtil botUtil;

    @Override
    public boolean handle(Update update, UserDto user) {
        String data = update.getCallbackQuery().getData();
        String languageStr = data.replaceFirst(command.getCommand(), "");
        LanguageCode language = LanguageCode.valueOf(languageStr);
        user.setLanguageCode(language);
        user = userService.updateUser(user);
        botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), CHANGE_LANGUAGE_SUCCESS);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return supportCallbackCommand(update, command);
    }

}
