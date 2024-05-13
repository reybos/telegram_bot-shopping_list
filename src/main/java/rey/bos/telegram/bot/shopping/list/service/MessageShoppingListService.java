package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;

import java.util.Optional;

public interface MessageShoppingListService {

    void saveShoppingListMessage(long chatId, long listId, int messageId);

    Optional<MessageShoppingList> findMessageByChatAndListAndId(long userId, long listId, int messageId);

}
