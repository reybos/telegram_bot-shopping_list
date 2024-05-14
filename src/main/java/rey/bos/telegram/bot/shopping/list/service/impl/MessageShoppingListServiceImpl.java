package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.MessageParams;
import rey.bos.telegram.bot.shopping.list.io.repository.MessageShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.service.MessageShoppingListService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageShoppingListServiceImpl implements MessageShoppingListService {

    private final MessageShoppingListRepository messageShoppingListRepository;

    @Override
    @Transactional
    public void saveShoppingListMessage(long userId, long listId, int messageId) {
        messageShoppingListRepository.deleteOldMessage(userId, listId);
        messageShoppingListRepository.save(
            MessageShoppingList.builder()
                .userId(userId)
                .shoppingListId(listId)
                .messageId(messageId)
                .build()
        );
    }

    @Override
    public Optional<MessageShoppingList> findMessageByUserAndListAndId(long userId, long listId, int messageId) {
        return messageShoppingListRepository.findByUserIdAndShoppingListIdAndMessageId(userId, listId, messageId);
    }

    @Override
    public List<MessageParams> findAllMessageByList(long listId) {
        return messageShoppingListRepository.findAllByShoppingListId(listId);
    }

}
