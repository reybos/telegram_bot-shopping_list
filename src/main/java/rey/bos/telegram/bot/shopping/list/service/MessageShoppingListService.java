package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.MessageParams;

import java.util.List;
import java.util.Optional;

public interface MessageShoppingListService {

    List<MessageParams> saveShoppingListMessage(long chatId, long listId, int messageId);

    Optional<MessageShoppingList> findMessageByUserAndListAndId(long userId, long listId, int messageId);

    List<MessageParams> findAllMessageByList(long listId);

}
