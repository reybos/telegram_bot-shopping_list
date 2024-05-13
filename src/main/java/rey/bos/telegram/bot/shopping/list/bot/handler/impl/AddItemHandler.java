package rey.bos.telegram.bot.shopping.list.bot.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddItemHandler extends BotHandler {

    public final static int MAX_ITEM_LENGTH = 30;
    public final static int MAX_ITEM_NUMBER = 30;

    private final BotUtil botUtil;
    private final ShoppingListService shoppingListService;

    @Override
    public boolean handle(Update update, UserDto user) {
        String item = update.getMessage().getText();
        if (item.length() > MAX_ITEM_LENGTH) {
            botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), ERROR_ITEM_ADD_TOO_LONG_ITEM);
            return true;
        }
        ShoppingList shoppingList;
        try {
            shoppingList = shoppingListService.findActiveList(user.getId());
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        if (shoppingList.getItems().size() >= MAX_ITEM_NUMBER) {
            botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), ERROR_ITEM_ADD_TOO_LONG_LIST);
            return true;
        }
        shoppingListService.addItem(shoppingList, item);
        botUtil.sendMessageByKey(user.getTelegramId(), user.getLanguageCode(), ACTION_ITEM_ADDED_TO_LIST);
        return true;
    }

    @Override
    public boolean support(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
            && (CollectionUtils.isEmpty(update.getMessage().getEntities())
                || update.getMessage().getEntities()
                    .stream()
                    .map(MessageEntity::getType)
                    .noneMatch(BOT_COMMAND_TYPE::equals));
    }

}
